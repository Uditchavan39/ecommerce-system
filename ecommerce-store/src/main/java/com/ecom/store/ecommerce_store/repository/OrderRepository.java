package com.ecom.store.ecommerce_store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.store.ecommerce_store.model.Order;
import com.ecom.store.ecommerce_store.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}
