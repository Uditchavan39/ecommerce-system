package com.ecom.store.ecommerce_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.store.ecommerce_store.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmail(String email);
}
