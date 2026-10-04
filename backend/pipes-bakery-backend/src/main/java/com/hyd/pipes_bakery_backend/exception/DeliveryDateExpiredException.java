package com.hyd.pipes_bakery_backend.exception;

/**
 * A failed payment is being retried, but the delivery date chosen at checkout no longer meets
 * the minimum preparation time: the customer must choose a new date before paying again.
 */
public class DeliveryDateExpiredException extends RuntimeException {
    public DeliveryDateExpiredException(String message) {
        super(message);
    }
}
