package com.ecom.store.ecommerce_store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ecom.store.ecommerce_store.model.Payment;
import com.ecom.store.ecommerce_store.model.PaymentStatus;

public class PaymentResponse {
    private Long id;
    private String transactionId;
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String message;

    // Constructors
    public PaymentResponse() {
    }

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.transactionId = payment.getTransactionId();
        this.orderId = payment.getOrder() != null ? payment.getOrder().getId() : null;
        this.amount = payment.getAmount();
        this.paymentMethod = payment.getPaymentMethod();
        this.status = payment.getStatus();
        this.paymentDate = payment.getPaymentDate();
    }

    public PaymentResponse(String transactionId, PaymentStatus status, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
