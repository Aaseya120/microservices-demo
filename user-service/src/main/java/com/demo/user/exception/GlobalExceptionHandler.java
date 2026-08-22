package com.demo.user.exception;

import com.demo.user.dto.Dtos.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserExceptions.UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(UserExceptions.UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(UserExceptions.DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(UserExceptions.DuplicateEmailException ex) {
        log.warn("Duplicate email: {}", ex.getMessage());
        return ResponseEntity.status(409).body(new ApiResponse<>(null, ex.getMessage(), false));
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
