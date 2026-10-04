package com.hyd.pipes_bakery_backend.email.resend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.hyd.pipes_bakery_backend.email.EmailMessage;
import com.hyd.pipes_bakery_backend.email.EmailProperties;
import com.hyd.pipes_bakery_backend.email.EmailSendException;
import com.hyd.pipes_bakery_backend.email.EmailSender;

/** Sends emails through the Resend HTTP API (https://resend.com/docs/api-reference/emails/send-email). */
@Component
@ConditionalOnProperty(name = "application.email.provider", havingValue = "resend")
public class ResendEmailSender implements EmailSender {

    private final EmailProperties emailProperties;
    private final RestClient restClient;

    public ResendEmailSender(EmailProperties emailProperties, RestClient.Builder restClientBuilder) {
        String apiKey = emailProperties.getResend().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "application.email.provider=resend requires application.email.resend.api-key (RESEND_API_KEY)");
        }

        this.emailProperties = emailProperties;
        this.restClient = restClientBuilder
                .baseUrl(emailProperties.getResend().getApiBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    @Override
    public void send(EmailMessage message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", emailProperties.getFrom());
        body.put("to", List.of(message.to()));
        body.put("subject", message.subject());
        body.put("html", message.html());
        if (message.text() != null) {
            body.put("text", message.text());
        }
        String replyTo = emailProperties.getReplyTo();
        if (replyTo != null && !replyTo.isBlank()) {
            body.put("reply_to", replyTo);
        }

        try {
            restClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                        if (message.idempotencyKey() != null) {
                            headers.set("Idempotency-Key", message.idempotencyKey());
                        }
                    })
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new EmailSendException(
                    "Resend rejected the email (HTTP " + e.getStatusCode().value() + "): " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new EmailSendException("Could not reach Resend", e);
        }
    }
}
