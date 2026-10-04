package com.hyd.pipes_bakery_backend.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "application.email")
@Validated
public class EmailProperties {

    /** Which {@link EmailSender} to use: "log" (default, writes to the console) or "resend". */
    @NotBlank
    private String provider = "log";

    /** Sender, e.g. "Melik Bakery &lt;no-reply@melikbakery.com&gt;". */
    @NotBlank
    private String from;

    /** Optional address customers' replies go to. */
    private String replyTo;

    @Valid
    private Resend resend = new Resend();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Resend getResend() {
        return resend;
    }

    public void setResend(Resend resend) {
        this.resend = resend;
    }

    public static class Resend {

        private String apiKey;

        private String apiBaseUrl = "https://api.resend.com";

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getApiBaseUrl() {
            return apiBaseUrl;
        }

        public void setApiBaseUrl(String apiBaseUrl) {
            this.apiBaseUrl = apiBaseUrl;
        }
    }
}
