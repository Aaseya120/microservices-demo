package com.demo.product.saga.command;

public record ReserveInventoryCommand(
        String orderId,
        String productId,
        int quantity
) {}
