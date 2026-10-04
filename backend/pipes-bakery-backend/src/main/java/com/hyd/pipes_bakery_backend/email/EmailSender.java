package com.hyd.pipes_bakery_backend.email;

/**
 * Sends transactional emails. The rest of the application depends only on this interface;
 * the implementation is chosen with {@code application.email.provider}. To add a provider,
 * implement this interface and annotate it with
 * {@code @ConditionalOnProperty(name = "application.email.provider", havingValue = "<name>")}.
 */
public interface EmailSender {

    /**
     * @throws EmailSendException if the provider rejects the email or cannot be reached
     */
    void send(EmailMessage message);
}
