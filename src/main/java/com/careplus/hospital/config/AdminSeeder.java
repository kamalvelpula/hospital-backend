package com.careplus.hospital.config;

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.careplus.hospital.entity.Admin;
import com.careplus.hospital.repository.AdminRepository;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;
    private final String fullName;

    public AdminSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                       @Value("${app.admin.username}") String username,
                       @Value("${app.admin.password:}") String password,
                       @Value("${app.admin.full-name}") String fullName) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminRepository.findByUsername(username).isPresent()) {
            return;
        }
        if (password == null || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            log.warn("No admin created: ADMIN_PASSWORD must be 8 to 72 bytes long");
            return;
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setFullName(fullName);
        adminRepository.save(admin);
        log.info("Admin account created");
    }
}