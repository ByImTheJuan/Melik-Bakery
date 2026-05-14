package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.hyd.pipes_bakery_backend.exception.LoginRateLimitException;

@ExtendWith(MockitoExtension.class)
class LoginRateLimitServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private LoginRateLimitService loginRateLimitService;

    @BeforeEach
    void setUp() {
        loginRateLimitService = new LoginRateLimitService(redisTemplate);
    }

    @Test
    void shouldLockLoginAfterFiveFailedAttemptsForSameIpAndEmail() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("login:failed-attempts:127.0.0.1:admin@example.com"))
                .thenReturn(1L, 2L, 3L, 4L, 5L);

        for (int i = 0; i < 5; i++) {
            loginRateLimitService.recordFailedAttempt("127.0.0.1", "Admin@Example.com");
        }

        verify(redisTemplate).expire(
                eq("login:failed-attempts:127.0.0.1:admin@example.com"),
                eq(Duration.ofMinutes(15))
        );
        verify(valueOperations).set(
                eq("login:lock:127.0.0.1:admin@example.com"),
                eq("locked"),
                eq(Duration.ofMinutes(15))
        );
    }

    @Test
    void shouldRejectLoginWhenLockKeyExists() {
        when(redisTemplate.hasKey("login:lock:127.0.0.1:admin@example.com")).thenReturn(true);

        assertThatThrownBy(() -> loginRateLimitService.assertLoginAllowed("127.0.0.1", "admin@example.com"))
                .isInstanceOf(LoginRateLimitException.class);
    }

    @Test
    void shouldRejectLoginWhenRedisRateWindowIsExceeded() {
        when(redisTemplate.hasKey("login:lock:127.0.0.1:admin@example.com")).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("login:rate:127.0.0.1:admin@example.com")).thenReturn(11L);

        assertThatThrownBy(() -> loginRateLimitService.assertLoginAllowed("127.0.0.1", "admin@example.com"))
                .isInstanceOf(LoginRateLimitException.class);
    }

    @Test
    void shouldClearAttemptAndLockKeysAfterSuccessfulLogin() {
        loginRateLimitService.clearFailedAttempts("127.0.0.1", "Admin@Example.com");

        verify(redisTemplate).delete("login:failed-attempts:127.0.0.1:admin@example.com");
        verify(redisTemplate).delete("login:lock:127.0.0.1:admin@example.com");
    }
}
