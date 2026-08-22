package com.demo.order.saga.command;

public record SendNotificationCommand(
        String orderId,
        String userId,
        String message
) {}
