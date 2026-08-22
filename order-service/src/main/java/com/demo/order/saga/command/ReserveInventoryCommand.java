package com.demo.order.saga.command;

public record ReserveInventoryCommand(
        String orderId,
        String productId,
        int quantity
) {}
