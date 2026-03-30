package com.ecom.store.ecommerce_store.dto;

import com.ecom.store.ecommerce_store.model.OrderItem;

public class OrderItemResponse {
    private Long id;
    private Integer quantity;
    private Double purchasePrice;
    private String productName;
    private String seller;

    public OrderItemResponse(OrderItem orderItem) {
        this.setId(orderItem.getId());
        this.setPurchasePrice(orderItem.getPurchasePrice());
        this.setQuantity(orderItem.getQuantity());
        this.setProductName(orderItem.getProduct().getName());
        this.setSeller(orderItem.getProduct().getSeller().getEmail());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

}
