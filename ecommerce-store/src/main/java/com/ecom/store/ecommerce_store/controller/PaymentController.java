package com.ecom.store.ecommerce_store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.store.ecommerce_store.dto.APIResponse;
import com.ecom.store.ecommerce_store.dto.PaymentRequest;
import com.ecom.store.ecommerce_store.dto.PaymentResponse;
import com.ecom.store.ecommerce_store.model.Payment;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.service.PaymentService;
import com.ecom.store.ecommerce_store.service.UserService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    /**
     * Process payment for an order
     * POST /api/payments/process/{orderId}
     * Request body: PaymentRequest { paymentMethod, amount }
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/process/{orderId}")
    public ResponseEntity<?> processPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest paymentRequest) {
        try {
            // Verify user is authenticated
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByEmail(userEmail);
            if (user == null) {
                return ResponseEntity.status(401).body(new APIResponse("Unauthorized", false));
            }

            // Process payment
            Payment payment = paymentService.processPayment(orderId, paymentRequest);
            PaymentResponse response = new PaymentResponse(payment);
            response.setMessage("Payment processed successfully");

            return ResponseEntity.ok(new APIResponse("Payment processed successfully", true, response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new APIResponse("Payment processing failed: " + e.getMessage(), false));
        }
    }

    /**
     * Get payment details by transaction ID
     * GET /api/payments/{transactionId}
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            // Verify user is authenticated
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByEmail(userEmail);
            if (user == null) {
                return ResponseEntity.status(401).body(new APIResponse("Unauthorized", false));
            }

            Payment payment = paymentService.getPaymentByTransactionId(transactionId);
            PaymentResponse response = new PaymentResponse(payment);

            return ResponseEntity.ok(new APIResponse("Payment details retrieved", true, response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new APIResponse("Error retrieving payment: " + e.getMessage(), false));
        }
    }
}
