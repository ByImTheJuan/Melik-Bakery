package com.hyd.pipes_bakery_backend.service;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.exception.LoginRateLimitException;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Service
public class LoginRateLimitService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration FAILED_ATTEMPT_WINDOW = Duration.ofMinutes(15);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);
    private static final int MAX_ATTEMPTS_PER_MINUTE = 10;

    private final StringRedisTemplate redisTemplate;
    private final Map<String, Bucket> requestBuckets = new ConcurrentHashMap<>();

    public LoginRateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void assertLoginAllowed(String ipAddress, String email) {
        String key = key(ipAddress, email);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey(key)))) {
            throw new LoginRateLimitException("Too many failed login attempts. Try again later.");
        }

        Long requests = redisTemplate.opsForValue().increment(rateKey(key));
        if (requests != null && requests == 1) {
            redisTemplate.expire(rateKey(key), Duration.ofMinutes(1));
        }

        if (requests != null && requests > MAX_ATTEMPTS_PER_MINUTE) {
            throw new LoginRateLimitException("Too many login attempts. Try again later.");
        }

        Bucket bucket = requestBuckets.computeIfAbsent(key, ignored -> Bucket.builder()
                .addLimit(Bandwidth.classic(
                        MAX_ATTEMPTS_PER_MINUTE,
                        Refill.greedy(MAX_ATTEMPTS_PER_MINUTE, Duration.ofMinutes(1))
                ))
                .build());

        if (!bucket.tryConsume(1)) {
            throw new LoginRateLimitException("Too many login attempts. Try again later.");
        }
    }

    public void recordFailedAttempt(String ipAddress, String email) {
        String key = key(ipAddress, email);
        String attemptsKey = attemptsKey(key);

        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, FAILED_ATTEMPT_WINDOW);
        }

        if (attempts != null && attempts >= MAX_FAILED_ATTEMPTS) {
            redisTemplate.opsForValue().set(lockKey(key), "locked", LOCK_DURATION);
            redisTemplate.delete(attemptsKey);
        }
    }

    public void clearFailedAttempts(String ipAddress, String email) {
        String key = key(ipAddress, email);
        redisTemplate.delete(attemptsKey(key));
        redisTemplate.delete(lockKey(key));
    }

    private String key(String ipAddress, String email) {
        String normalizedIp = normalize(ipAddress);
        String normalizedEmail = normalize(email);

        return normalizedIp + ":" + normalizedEmail;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String attemptsKey(String key) {
        return "login:failed-attempts:" + key;
    }

    private String lockKey(String key) {
        return "login:lock:" + key;
    }

    private String rateKey(String key) {
        return "login:rate:" + key;
    }
}
