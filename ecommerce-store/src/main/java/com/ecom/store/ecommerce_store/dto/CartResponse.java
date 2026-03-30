package com.ecom.store.ecommerce_store.dto;

import java.util.List;

import com.ecom.store.ecommerce_store.model.Cart;

public class CartResponse {
    private List<CartItemResponse> items;

    public CartResponse(List<CartItemResponse> items) {
        this.items = items;
    }

    public CartResponse(Cart cart) {
        setItems(cart.getItems().stream().map(CartItemResponse::new).toList());
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

}
