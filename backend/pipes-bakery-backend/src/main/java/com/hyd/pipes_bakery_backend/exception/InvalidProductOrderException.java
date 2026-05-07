package com.hyd.pipes_bakery_backend.exception;

public class InvalidProductOrderException extends RuntimeException {
    public InvalidProductOrderException(String message) {
        super(message);
    }
}
