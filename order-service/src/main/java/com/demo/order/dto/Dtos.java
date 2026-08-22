package com.demo.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Dtos {

    public record OrderRequest(
            @NotBlank String userId,
            @NotBlank String productId,
            @Min(1) int qty
    ) {}

    public record ApiResponse<T>(T data, String message, boolean success) {}
}
