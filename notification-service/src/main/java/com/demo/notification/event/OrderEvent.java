package com.demo.notification.event;

import java.time.Instant;

public record OrderEvent(
        String orderId,
        String eventType,   // ORDER_CREATED | ORDER_CONFIRMED | ORDER_CANCELLED
        String userId,
        Instant timestamp
) {
    public OrderEvent(String orderId, String eventType, String userId) {
        this(orderId, eventType, userId, Instant.now());
    }
}
