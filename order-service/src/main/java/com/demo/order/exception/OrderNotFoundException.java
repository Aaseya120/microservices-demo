package com.demo.order.exception;

public final class OrderNotFoundException extends OrderException {
    public OrderNotFoundException(String id) {
        super("Order not found: " + id);
    }
}
