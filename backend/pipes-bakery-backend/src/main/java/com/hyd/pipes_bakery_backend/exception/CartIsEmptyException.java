package com.hyd.pipes_bakery_backend.exception;

public class CartIsEmptyException extends RuntimeException {
    
    public CartIsEmptyException(String message) {
        super(message);
    }
}
