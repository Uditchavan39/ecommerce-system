package com.ecom.store.ecommerce_store.dto;

import com.ecom.store.ecommerce_store.model.CartItem;

public class CartItemResponse {
    private Integer quantity;
    private ProductResponse product;
    private Long id;

    public CartItemResponse(CartItem cartItem) {
        this.product = new ProductResponse.Builder(cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getProduct().getSeller().getEmail())
                .setDescription(cartItem.getProduct().getDescription())
                .setCategory(cartItem.getProduct().getCategory())
                .setImages(cartItem.getProduct().getImages().stream().map(image -> image.getImageUrl()).toList())
                .build();
        this.setQuantity(cartItem.getQuantity());
        this.setId(cartItem.getId());
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
