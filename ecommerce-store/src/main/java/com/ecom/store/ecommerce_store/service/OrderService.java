package com.ecom.store.ecommerce_store.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.store.ecommerce_store.model.Cart;
import com.ecom.store.ecommerce_store.model.CartItem;
import com.ecom.store.ecommerce_store.model.Order;
import com.ecom.store.ecommerce_store.model.OrderItem;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.repository.CartRepository;
import com.ecom.store.ecommerce_store.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private CartService cartService;
    private UserService userService;

    public OrderService(OrderRepository orderRepository, CartService cartService, UserService userService,
            CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.cartRepository = cartRepository;
    }

    public List<Order> getOrders(Long userId) {
        User user = userService.getUserById(userId);
        List<Order> order = orderRepository.findByUser(user);
        return order != null ? order : new ArrayList<>();
    }

    @Transactional
    public Order checkoutCart(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            return null;
        }
        List<CartItem> items = cart.getItems();
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setPurchasePrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        User user = userService.getUserById(userId);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("CREATED");
        order.setPaymentMethod("CARD");// TODO:hardcoded for now
        order.setItems(orderItems);
        order.setUser(user);
        orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);
        return order;
    }
}
