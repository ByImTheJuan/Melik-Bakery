package com.hyd.pipes_bakery_backend.email;

/**
 * A provider-independent email. The sender address comes from configuration, not from here.
 *
 * @param idempotencyKey identifies this logical email so a provider that supports it never
 *                       delivers it twice; providers without that feature can ignore it
 */
public record EmailMessage(
        String to,
        String subject,
        String html,
        String text,
        String idempotencyKey
) {
}
