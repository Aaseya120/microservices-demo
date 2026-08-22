package com.demo.notification.saga.command;

public record SendNotificationCommand(
        String orderId,
        String userId,
        String message
) {}
