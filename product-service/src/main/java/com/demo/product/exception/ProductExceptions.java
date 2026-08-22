package com.demo.product.exception;

import com.demo.product.dto.Dtos.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

public class ProductExceptions {

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String id) {
            super("Product not found: " + id);
        }
    }

    @RestControllerAdvice
    @Slf4j
    public static class GlobalExceptionHandler {

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleNotFound(ProductNotFoundException ex) {
            log.warn("Product not found: {}", ex.getMessage());
            return ResponseEntity.status(404).body(new ApiResponse<>(null, ex.getMessage(), false));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(new ApiResponse<>(errors, "Validation failed", false));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex) {
            log.error("Unexpected error", ex);
            return ResponseEntity.status(500).body(new ApiResponse<>(null, "Internal server error", false));
        }
    }
}
