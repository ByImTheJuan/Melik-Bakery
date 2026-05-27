package com.hyd.pipes_bakery_backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class JwtRevocationService {

    private static final String REVOKED_TOKEN_PREFIX = "jwt:revoked:";

    private final StringRedisTemplate redisTemplate;

    public JwtRevocationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void revoke(String token, Duration remainingValidity) {
        if (token == null || token.isBlank() || remainingValidity.isZero() || remainingValidity.isNegative()) {
            return;
        }

        redisTemplate.opsForValue().set(key(token), "revoked", remainingValidity);
    }

    public boolean isRevoked(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(token)));
    }

    private String key(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return REVOKED_TOKEN_PREFIX
                    + HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is required to revoke JWTs", exception);
        }
    }
}
