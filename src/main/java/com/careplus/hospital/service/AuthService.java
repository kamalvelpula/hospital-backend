package com.careplus.hospital.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import com.careplus.hospital.dto.LoginRequest;
import com.careplus.hospital.dto.LoginResponse;
import com.careplus.hospital.entity.Admin;
import com.careplus.hospital.exception.UnauthorizedException;
import com.careplus.hospital.repository.AdminRepository;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    private final LoginAttemptService loginAttemptService;
    private final long expiryMinutes;
    private final String dummyHash;

    public AuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder,
                       Clock clock, LoginAttemptService loginAttemptService,
                       @Value("${app.jwt.expiry-minutes:120}") long expiryMinutes) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
        this.loginAttemptService = loginAttemptService;
        this.expiryMinutes = expiryMinutes;
        // Unknown usernames take the same time as wrong passwords
        this.dummyHash = passwordEncoder.encode("not-a-real-password");
    }

    public LoginResponse login(LoginRequest req, String clientKey) {
        loginAttemptService.checkAllowed(clientKey);

        Optional<Admin> found = adminRepository.findByUsername(req.username().trim());
        String hash = found.map(Admin::getPasswordHash).orElse(dummyHash);
        boolean passwordOk = passwordEncoder.matches(req.password(), hash);

        if (found.isEmpty() || !passwordOk) {
            loginAttemptService.recordFailure(clientKey);
            log.warn("Failed admin login attempt");
            throw new UnauthorizedException("Invalid username or password");
        }
        loginAttemptService.recordSuccess(clientKey);

        Admin admin = found.get();
        Instant now = Instant.now(clock);
        Instant expiry = now.plusSeconds(expiryMinutes * 60);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("careplus-hospital")
                .subject(admin.getUsername())
                .issuedAt(now)
                .expiresAt(expiry)
                .claim("roles", List.of("ADMIN"))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        log.info("Admin login successful");
        return new LoginResponse(token, "Bearer", expiryMinutes * 60, admin.getUsername(), admin.getFullName());
    }
}