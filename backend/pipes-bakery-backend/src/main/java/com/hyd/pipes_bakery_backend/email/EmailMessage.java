package com.hyd.pipes_bakery_backend.email;

import java.util.List;

/**
 * A provider-independent email. The sender address comes from configuration, not from here.
 *
 * @param idempotencyKey identifies this logical email so a provider that supports it never
 *                       delivers it twice; providers without that feature can ignore it
 * @param inlineImages   images embedded in the HTML, referenced there as {@code cid:<contentId>}
 */
public record EmailMessage(
        String to,
        String subject,
        String html,
        String text,
        String idempotencyKey,
        List<InlineImage> inlineImages
) {

    public EmailMessage {
        inlineImages = inlineImages == null ? List.of() : List.copyOf(inlineImages);
    }

    public EmailMessage(String to, String subject, String html, String text, String idempotencyKey) {
        this(to, subject, html, text, idempotencyKey, List.of());
    }
}
