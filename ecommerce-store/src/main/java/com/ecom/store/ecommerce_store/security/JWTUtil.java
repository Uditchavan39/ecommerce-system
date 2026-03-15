package com.ecom.store.ecommerce_store.security;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.ecom.store.ecommerce_store.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
    private final String SECRET = "mySuperSecretKeyForJwtTokenMySuperSecretKey123456789";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(Long userId, String email, List<Role> roles) {
        List<String> roleNames = roles
                .stream()
                .map(Role::getRoleType)
                .toList();

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("roles", roleNames)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
