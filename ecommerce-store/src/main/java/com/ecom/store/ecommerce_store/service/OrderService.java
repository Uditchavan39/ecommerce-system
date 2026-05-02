package com.ecom.store.ecommerce_store.service;

import com.ecom.store.ecommerce_store.controller.AuthController;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.store.ecommerce_store.model.Cart;
import com.ecom.store.ecommerce_store.model.CartItem;
import com.ecom.store.ecommerce_store.model.Order;
import com.ecom.store.ecommerce_store.model.OrderItem;
import com.ecom.store.ecommerce_store.model.OrderStatus;
import com.ecom.store.ecommerce_store.model.Product;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.repository.CartRepository;
import com.ecom.store.ecommerce_store.repository.OrderRepository;
import com.ecom.store.ecommerce_store.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final AuthController authController;
    private final InventoryService inventoryService;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private CartService cartService;
    private UserService userService;
    private ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartService cartService, UserService userService,
            CartRepository cartRepository, ProductRepository productRepository, InventoryService inventoryService,
            AuthController authController) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.authController = authController;
    }

    public List<Order> getOrders(Long userId) {
        User user = userService.getUserById(userId);
        List<Order> order = orderRepository.findByUser(user);
        return order != null ? order : new ArrayList<>();
    }

}
