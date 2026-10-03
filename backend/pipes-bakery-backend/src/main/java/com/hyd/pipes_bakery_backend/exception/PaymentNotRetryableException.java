package com.hyd.pipes_bakery_backend.exception;

public class PaymentNotRetryableException extends RuntimeException {

    public PaymentNotRetryableException(String message) {
        super(message);
    }
}
