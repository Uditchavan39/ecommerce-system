package com.ecom.store.ecommerce_store.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ecom.store.ecommerce_store.dto.UserRegistrationDto;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private UserService userService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRegistrationDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        User savedUser = userService.register(user);
        return "User registered successfully";
    }

    // TODO: Implement login functionality properly with JWT or session management
    @PostMapping("/login")
    public String login(@RequestBody UserRegistrationDto dto) {
        User entity = userService.findByEmail(dto.getEmail());
        if (entity == null || !passwordEncoder.matches(dto.getPassword(), entity.getPassword())) {
            return "Invalid email or password";
        }
        return "Login successful";
    }

}
