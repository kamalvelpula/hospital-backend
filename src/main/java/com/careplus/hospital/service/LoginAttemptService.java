package com.careplus.hospital.service;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.careplus.hospital.exception.TooManyRequestsException;

@Service
public class LoginAttemptService {

    private static final long WINDOW_MS = 15 * 60 * 1000L;

    private static class Attempt {
        int count;
        long firstFailureAt;
        long blockedUntil;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final Clock clock;
    private final int maxAttempts;
    private final long blockMs;

    public LoginAttemptService(Clock clock,
                               @Value("${app.security.max-login-attempts:5}") int maxAttempts,
                               @Value("${app.security.login-block-minutes:15}") long blockMinutes) {
        this.clock = clock;
        this.maxAttempts = maxAttempts;
        this.blockMs = blockMinutes * 60 * 1000L;
    }

    public void checkAllowed(String key) {
        Attempt a = attempts.get(key);
        if (a == null) {
            return;
        }
        long now = clock.millis();
        synchronized (a) {
            if (a.blockedUntil > now) {
                throw new TooManyRequestsException("Too many failed login attempts. Please try again later.");
            }
        }
    }

    public void recordFailure(String key) {
        long now = clock.millis();
        if (attempts.size() > 10_000) {
            attempts.entrySet().removeIf(e -> e.getValue().blockedUntil < now && now - e.getValue().firstFailureAt > WINDOW_MS);
        }
        Attempt a = attempts.computeIfAbsent(key, k -> new Attempt());
        synchronized (a) {
            if (now - a.firstFailureAt > WINDOW_MS) {
                a.count = 0;
                a.firstFailureAt = now;
            }
            a.count++;
            if (a.count >= maxAttempts) {
                a.blockedUntil = now + blockMs;
                a.count = 0;
            }
        }
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }
}