package com.ecom.store.ecommerce_store.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private String paymentMethod; // CARD, UPI, COD
    private BigDecimal amount;

    // Constructors
    public PaymentRequest() {
    }

    public PaymentRequest(String paymentMethod, BigDecimal amount) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    // Getters and Setters
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
