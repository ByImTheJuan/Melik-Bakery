package com.hyd.pipes_bakery_backend.service;

import java.time.Duration;
import java.util.Locale;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.exception.UploadRateLimitException;

/**
 * Limits the public custom cake photo uploads per IP so the endpoint can't be used to fill the disk.
 */
@Service
public class UploadRateLimitService {

    private static final int MAX_UPLOADS_PER_WINDOW = 15;
    private static final Duration WINDOW = Duration.ofHours(1);

    private final StringRedisTemplate redisTemplate;

    public UploadRateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void assertUploadAllowed(String ipAddress) {
        String key = "custom-cake-upload:" + normalize(ipAddress);

        Long uploads = redisTemplate.opsForValue().increment(key);
        if (uploads != null && uploads == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        if (uploads != null && uploads > MAX_UPLOADS_PER_WINDOW) {
            throw new UploadRateLimitException("Too many image uploads. Try again later.");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }
}
