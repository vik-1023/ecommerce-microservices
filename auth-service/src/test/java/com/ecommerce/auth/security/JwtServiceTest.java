package com.ecommerce.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "7fK9mQ2xL8vR4nT6pZ1sW5yH3cJ8dN0aB6gE9uI2oP4rS7tV"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expire",
                3600000L
        );
    }

    @Test
    void generateToken_ShouldReturnToken() {

        String token = jwtService.generateToken("vikram@gmail.com","USER");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {

        String token = jwtService.generateToken("vikram@gmail.com","USER");

        String email = jwtService.extractEmail(token);

        assertEquals("vikram@gmail.com", email);
    }

    @Test
    void extractEmail_WhenTokenIsExpired_ShouldThrowException() {

        SecretKey key = Keys.hmacShaKeyFor(
                "7fK9mQ2xL8vR4nT6pZ1sW5yH3cJ8dN0aB6gE9uI2oP4rS7tV"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("vikram@gmail.com")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.extractEmail(token)
        );
    }
}