package com.hyd.pipes_bakery_backend.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.springframework.core.io.ClassPathResource;

/**
 * An image sent inside the email (MIME "Content-ID") instead of being linked, so it shows without
 * depending on a public URL. The HTML references it as {@code <img src="cid:contentId">}.
 */
public record InlineImage(String contentId, String filename, String contentType, byte[] content) {

    public static InlineImage fromClasspath(String path, String contentId, String contentType) {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream in = resource.getInputStream()) {
            return new InlineImage(contentId, resource.getFilename(), contentType, in.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read email image " + path, e);
        }
    }
}
