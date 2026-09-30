package com.hyd.pipes_bakery_backend.exception;

public class OrderNotPayableException extends RuntimeException {

    public OrderNotPayableException(String message) {
        super(message);
    }
}
