package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.hyd.pipes_bakery_backend.config.JwtProperties;

class JwtServiceTest {

    @Test
    void shouldGenerateTokenAndExtractUsernameAndRoles() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("melik-bakery-development-secret-key-change-me-please");
        properties.setExpiration(Duration.ofDays(1));

        JwtService jwtService = new JwtService(properties);
        UserDetails userDetails = User.withUsername("admin@melikbakery.com")
                .password("$2a$10$hashedPassword")
                .authorities("ROLE_ADMIN")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("admin@melikbakery.com");
        assertThat(jwtService.extractRoles(token)).containsExactly("ROLE_ADMIN");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void shouldIgnoreUnexpectedNonStringRoleEntries() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("melik-bakery-development-secret-key-change-me-please");
        properties.setExpiration(Duration.ofDays(1));

        JwtService jwtService = new JwtService(properties);
        UserDetails userDetails = User.withUsername("admin@melikbakery.com")
                .password("$2a$10$hashedPassword")
                .authorities("ROLE_ADMIN")
                .build();

        String token = io.jsonwebtoken.Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", List.of("ROLE_ADMIN", 5, true))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        properties.getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8)
                ))
                .compact();

        assertThat(jwtService.extractRoles(token)).containsExactly("ROLE_ADMIN");
    }
}
