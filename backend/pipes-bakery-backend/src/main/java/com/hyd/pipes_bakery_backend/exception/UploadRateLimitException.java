package com.hyd.pipes_bakery_backend.exception;

public class UploadRateLimitException extends RuntimeException {
    public UploadRateLimitException(String message) {
        super(message);
    }
}
