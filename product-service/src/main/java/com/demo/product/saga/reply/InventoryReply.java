package com.demo.product.saga.reply;

public record InventoryReply(
        String orderId,
        String status, // RESERVED or FAILED
        String reason
) {}
