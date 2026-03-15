package com.ecom.store.ecommerce_store.dto;

public class APIResponse {
    private String message;
    private int status;

    public APIResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

}
