package com.ecom.store.ecommerce_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.store.ecommerce_store.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
