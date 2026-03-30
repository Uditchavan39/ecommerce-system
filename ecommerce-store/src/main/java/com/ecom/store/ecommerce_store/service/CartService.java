package com.ecom.store.ecommerce_store.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecom.store.ecommerce_store.model.Cart;
import com.ecom.store.ecommerce_store.model.CartItem;
import com.ecom.store.ecommerce_store.model.Product;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.repository.CartRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {
    private CartRepository cartRepository;
    private UserService userService;
    private ProductService productService;

    public CartService(CartRepository cartRepository, UserService userService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.productService = productService;
    }

    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        return cart != null ? cart : new Cart();
    }

    @Transactional
    public CartItem addCartItem(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            User user = userService.getUserById(userId);
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
        }
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int existingQuantity = item.getQuantity() != null ? item.getQuantity() : 0;
            int newQuantity = quantity != null ? quantity : 1;
            item.setQuantity(existingQuantity + newQuantity);
            cartRepository.save(cart);
            return item;
        }
        CartItem cartItem = new CartItem();
        Product product = productService.getProductById(productId);
        if (product == null) {
            return null;
        }
        cartItem.setQuantity(quantity);
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cart.getItems().add(cartItem);
        cartRepository.save(cart);
        return cartItem;
    }

    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
            cartRepository.save(cart);
        }
    }

}
