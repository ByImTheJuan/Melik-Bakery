package com.hyd.pipes_bakery_backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.hyd.pipes_bakery_backend.config.WompiProperties;
import com.hyd.pipes_bakery_backend.dto.payment.WompiTransactionDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WompiClientImpl implements WompiClient {

    private final WompiProperties wompiProperties;
    private final RestClient restClient;

    public WompiClientImpl(WompiProperties wompiProperties, RestClient.Builder restClientBuilder) {
        this.wompiProperties = wompiProperties;
        this.restClient = restClientBuilder.baseUrl(wompiProperties.getApiBaseUrl()).build();
    }

    @Override
    public String buildIntegritySignature(String reference, long amountInCents, String currency) {
        String raw = reference + amountInCents + currency + wompiProperties.getIntegritySecret();
        return sha256Hex(raw);
    }

    @Override
    public String buildCheckoutUrl(String reference, long amountInCents, String currency) {
        String signature = buildIntegritySignature(reference, amountInCents, currency);
        String redirectUrl = UriComponentsBuilder.fromUriString(wompiProperties.getRedirectUrl())
                .pathSegment(reference)
                .toUriString();

        return UriComponentsBuilder.fromUriString(wompiProperties.getCheckoutBaseUrl())
                .queryParam("public-key", wompiProperties.getPublicKey())
                .queryParam("currency", currency)
                .queryParam("amount-in-cents", amountInCents)
                .queryParam("reference", reference)
                .queryParam("redirect-url", redirectUrl)
                .queryParam("signature:integrity", signature)
                .encode()
                .toUriString();
    }

    @Override
    public boolean verifyEventChecksum(WompiWebhookEventDTO event) {
        if (event.getSignature() == null || event.getSignature().getChecksum() == null) {
            return false;
        }

        StringBuilder raw = new StringBuilder();
        for (String property : event.getSignature().getProperties()) {
            raw.append(resolveProperty(event.getData(), property));
        }
        raw.append(event.getTimestamp());
        raw.append(wompiProperties.getEventsSecret());

        String expectedChecksum = sha256Hex(raw.toString());
        return expectedChecksum.equalsIgnoreCase(event.getSignature().getChecksum());
    }

    @Override
    public WompiTransactionDTO fetchTransaction(String wompiTransactionId) {
        TransactionEnvelope envelope = restClient.get()
                .uri("/transactions/{id}", wompiTransactionId)
                .retrieve()
                .body(TransactionEnvelope.class);

        return envelope != null ? envelope.data : null;
    }

    private String resolveProperty(JsonNode data, String dotPath) {
        JsonNode current = data;
        for (String segment : dotPath.split("\\.")) {
            current = current != null ? current.path(segment) : null;
        }
        return current != null ? current.asText("") : "";
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private static class TransactionEnvelope {
        public WompiTransactionDTO data;
    }
}
