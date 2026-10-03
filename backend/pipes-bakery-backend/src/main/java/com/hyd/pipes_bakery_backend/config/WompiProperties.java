package com.hyd.pipes_bakery_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "application.payments.wompi")
@Validated
public class WompiProperties {

    @NotBlank
    private String publicKey;

    @NotBlank
    private String integritySecret;

    @NotBlank
    private String eventsSecret;

    @NotBlank
    private String apiBaseUrl;

    @NotBlank
    private String checkoutBaseUrl;

    @NotBlank
    private String redirectUrl;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getIntegritySecret() {
        return integritySecret;
    }

    public void setIntegritySecret(String integritySecret) {
        this.integritySecret = integritySecret;
    }

    public String getEventsSecret() {
        return eventsSecret;
    }

    public void setEventsSecret(String eventsSecret) {
        this.eventsSecret = eventsSecret;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getCheckoutBaseUrl() {
        return checkoutBaseUrl;
    }

    public void setCheckoutBaseUrl(String checkoutBaseUrl) {
        this.checkoutBaseUrl = checkoutBaseUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
