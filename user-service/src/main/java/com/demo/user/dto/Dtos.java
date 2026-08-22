package com.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Dtos {

    public record UserRequest(
            @NotBlank @Email String email,
            @NotBlank String fullName,
            @NotBlank @Size(min = 6, message = "password must be at least 6 characters") String password
    ) {}

    public record UserResponse(String id, String email, String fullName, String role) {}

    public record ApiResponse<T>(T data, String message, boolean success) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(String accessToken, UserResponse user) {}
}
