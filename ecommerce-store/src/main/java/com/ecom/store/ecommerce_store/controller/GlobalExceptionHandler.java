package com.ecom.store.ecommerce_store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecom.store.ecommerce_store.dto.APIResponse;
import com.ecom.store.ecommerce_store.exception.InsufficientStockException;
import com.ecom.store.ecommerce_store.exception.OrderNotFoundException;
import com.ecom.store.ecommerce_store.exception.PaymentFailedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle OrderNotFoundException
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<APIResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        return ResponseEntity.status(404)
                .body(new APIResponse(ex.getMessage(), false));
    }

    /**
     * Handle PaymentFailedException
     */
    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<APIResponse> handlePaymentFailedException(PaymentFailedException ex) {
        return ResponseEntity.status(402)
                .body(new APIResponse(ex.getMessage(), false));
    }

    /**
     * Handle InsufficientStockException
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<APIResponse> handleInsufficientStockException(InsufficientStockException ex) {
        return ResponseEntity.status(400)
                .body(new APIResponse(ex.getMessage(), false));
    }

    /**
     * Handle generic RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(400)
                .body(new APIResponse("Error: " + ex.getMessage(), false));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleException(Exception ex) {
        return ResponseEntity.status(500)
                .body(new APIResponse("Internal server error: " + ex.getMessage(), false));
    }
}
