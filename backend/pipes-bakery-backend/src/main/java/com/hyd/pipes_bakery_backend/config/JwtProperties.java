package com.hyd.pipes_bakery_backend.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    @NotNull
    private Duration expiration = Duration.ofDays(1);

    public String getSecret() {
        return secret;
    }

    public Duration getExpiration() {
        return expiration;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setExpiration(Duration expiration) {
        this.expiration = expiration;
    }
}
