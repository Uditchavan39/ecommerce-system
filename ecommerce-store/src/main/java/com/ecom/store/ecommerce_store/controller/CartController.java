package com.ecom.store.ecommerce_store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.store.ecommerce_store.dto.CartItemRequest;
import com.ecom.store.ecommerce_store.dto.CartItemResponse;
import com.ecom.store.ecommerce_store.dto.CartResponse;
import com.ecom.store.ecommerce_store.model.Cart;
import com.ecom.store.ecommerce_store.model.CartItem;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.service.CartService;
import com.ecom.store.ecommerce_store.service.UserService;

@RequestMapping("/api/cart")
@RestController
public class CartController {
    private CartService cartService;
    private UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<CartResponse> getCart() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }
        Cart cart = cartService.getCartByUserId(user.getId());
        return ResponseEntity.ok(new CartResponse(cart));
    }

    // handles both create and update.
    @PostMapping("/")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest cartItemRequest) {
        if (cartItemRequest.getProductId() == null || cartItemRequest.getQuantity() == null) {
            return ResponseEntity.status(400).body("Product ID and quantity are required");
        }
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }
        CartItem cartItem = cartService.addCartItem(user.getId(), cartItemRequest.getProductId(),
                cartItemRequest.getQuantity());
        if (cartItem == null) {
            return ResponseEntity.badRequest().body("Failed to Add Product to Cart");
        }
        return ResponseEntity.ok(new CartItemResponse(cartItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable("id") Long cartItemId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail);
        if (user == null)
            return ResponseEntity.status(404).body("Please Login Or Register First");
        try {
            cartService.removeCartItem(user.getId(), cartItemId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("deletion Failed");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Cart Item Successfully Deleted.");
    }

}
