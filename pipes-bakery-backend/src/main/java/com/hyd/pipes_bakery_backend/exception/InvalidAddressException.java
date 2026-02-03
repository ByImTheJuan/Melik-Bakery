package com.hyd.pipes_bakery_backend.exception;

public class InvalidAddressException extends RuntimeException {
    
    public InvalidAddressException(String message) {
        super(message);
    }
}
