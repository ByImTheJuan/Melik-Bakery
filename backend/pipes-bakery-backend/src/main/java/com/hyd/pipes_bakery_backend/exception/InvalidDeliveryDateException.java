package com.hyd.pipes_bakery_backend.exception;

public class InvalidDeliveryDateException extends RuntimeException {
    public InvalidDeliveryDateException(String message) {
        super(message);
    }
}
