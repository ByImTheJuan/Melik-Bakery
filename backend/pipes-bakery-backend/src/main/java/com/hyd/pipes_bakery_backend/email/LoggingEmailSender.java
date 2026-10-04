package com.hyd.pipes_bakery_backend.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Default sender for development and tests: writes the email to the log instead of sending it. */
@Component
@ConditionalOnProperty(name = "application.email.provider", havingValue = "log", matchIfMissing = true)
public class LoggingEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

    private final EmailProperties emailProperties;

    public LoggingEmailSender(EmailProperties emailProperties) {
        this.emailProperties = emailProperties;
    }

    @Override
    public void send(EmailMessage message) {
        log.info("[email not sent, provider=log] from={} to={} subject=\"{}\"\n{}",
                emailProperties.getFrom(), message.to(), message.subject(), message.text());
    }
}
