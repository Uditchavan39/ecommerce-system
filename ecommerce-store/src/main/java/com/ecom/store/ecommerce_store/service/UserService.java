package com.ecom.store.ecommerce_store.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        try {
            return userRepository.findByEmail(email).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
