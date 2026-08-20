package com.ecommerce.apigateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        HttpMethod method = exchange.getRequest()
                .getMethod();

        // Public APIs
        if (path.equals("/api/auth/login")
                || path.equals("/api/auth/register")) {

            return chain.filter(exchange);
        }

        // Get Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Authorization header missing
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        // Validate JWT
        if (!jwtService.isTokenValid(token)) {

            return unauthorized(exchange);
        }

        // Extract role
        String role = jwtService.extractRole(token);

        // Check authorization
        if (!isAuthorized(path, method, role)) {

            return forbidden(exchange);
        }

        return chain.filter(exchange);
    }

    private boolean isAuthorized(
            String path,
            HttpMethod method,
            String role) {

        // Product APIs
        if (path.startsWith("/api/products")) {

            // GET → CUSTOMER + ADMIN
            if (method == HttpMethod.GET) {
                return role.equals("CUSTOMER")
                        || role.equals("ADMIN");
            }

            // POST, PUT, DELETE → ADMIN
            return role.equals("ADMIN");
        }


        // Unknown protected API
        return false;
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(
            ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.FORBIDDEN);

        return exchange.getResponse().setComplete();
    }
}