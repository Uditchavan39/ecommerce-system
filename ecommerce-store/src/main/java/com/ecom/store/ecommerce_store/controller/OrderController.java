package com.ecom.store.ecommerce_store.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.store.ecommerce_store.dto.OrderResponse;
import com.ecom.store.ecommerce_store.model.Order;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.service.OrderService;
import com.ecom.store.ecommerce_store.service.UserService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private OrderService orderService;
    private UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/")
    public ResponseEntity<?> getOrders() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }
        List<Order> orders = orderService.getOrders(user.getId());
        List<OrderResponse> orderResponses = orders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(orderResponses);
    }

}
