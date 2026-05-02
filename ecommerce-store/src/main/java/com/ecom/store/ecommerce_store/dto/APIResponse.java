package com.ecom.store.ecommerce_store.dto;

public class APIResponse {
    private String message;
    private int status;
    private boolean success;
    private Object data;

    // Constructor for backward compatibility
    public APIResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.success = status < 400;
    }

    // Constructor with success flag
    public APIResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.status = success ? 200 : 400;
    }

    // Constructor with success flag and data
    public APIResponse(String message, boolean success, Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
        this.status = success ? 200 : 400;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
