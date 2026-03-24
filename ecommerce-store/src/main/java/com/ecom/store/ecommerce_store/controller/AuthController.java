package com.ecom.store.ecommerce_store.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.store.ecommerce_store.dto.APIResponse;
import com.ecom.store.ecommerce_store.dto.LoginRequest;
import com.ecom.store.ecommerce_store.dto.LoginResponse;
import com.ecom.store.ecommerce_store.dto.UserRegistrationDto;
import com.ecom.store.ecommerce_store.model.Role;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.security.JWTUtil;
import com.ecom.store.ecommerce_store.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private UserService userService;
    private final JWTUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse> register(@RequestBody UserRegistrationDto dto) {
        if (dto.getFirstName() == null || dto.getEmail() == null || dto.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse("all fields are required : firstName, email, password", 400));
        }
        if (userService.findByEmail(dto.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new APIResponse("user already exists with this email", 409));
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        // assign default role as CUSTOMER & SELLER
        List<Role> roles = getRoles(dto, user);
        if (roles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse("Invalid user type", 400));
        }
        user.setRoles(roles);
        userService.register(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse("user registered successfully", 201));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse("Email and password are required", 400));
        }

        User user = userService.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse("Invalid email or password", 401));
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRoles());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    private List<Role> getRoles(UserRegistrationDto dto, User user) {
        List<Role> roles = new ArrayList<>();
        if (dto.getRoles() == null || dto.getRoles().contains("USER")) {
            Role role = new Role();
            role.setRoleType("USER");
            role.setUser(user);
            roles.add(role);
        }
        if (dto.getRoles().contains("SELLER")) {
            Role role = new Role();
            role.setRoleType("SELLER");
            role.setUser(user);
            roles.add(role);
        }
        return roles;
    }

}
