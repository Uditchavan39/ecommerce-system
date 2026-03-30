package com.ecom.store.ecommerce_store.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.ecom.store.ecommerce_store.model.Order;

public class OrderResponse {
    private List<OrderItemResponse> orderItem;
    private Long id;

    public OrderResponse(Order order) {
        this.setOrderItem(order.getItems().stream().map(OrderItemResponse::new)
                .collect(Collectors.toList()));
        this.setId(order.getId());
    }

    public List<OrderItemResponse> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItemResponse> orderResponses) {
        this.orderItem = orderResponses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
