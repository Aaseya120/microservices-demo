package com.demo.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class Dtos {

    public record ProductRequest(
            @NotBlank String name,
            String description,
            @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal price,
            @NotNull @Min(0) Integer stockQuantity
    ) {}

    public record ApiResponse<T>(T data, String message, boolean success) {}
}
