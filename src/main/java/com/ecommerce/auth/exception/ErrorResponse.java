package com.ecommerce.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    LocalDateTime timestamp;
    private Map<String, String> errors;
}
