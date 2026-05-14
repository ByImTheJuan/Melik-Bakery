package com.hyd.pipes_bakery_backend.exception;

public class LoginRateLimitException extends RuntimeException {

    public LoginRateLimitException(String message) {
        super(message);
    }
}
