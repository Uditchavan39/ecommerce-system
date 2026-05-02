package com.ecom.store.ecommerce_store.exception;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException(Long orderId, String reason) {
        super("Payment failed for order " + orderId + ". Reason: " + reason);
    }
}
