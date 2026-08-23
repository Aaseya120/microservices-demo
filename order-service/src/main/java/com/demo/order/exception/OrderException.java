package com.demo.order.exception;

public abstract sealed class OrderException extends RuntimeException permits OrderNotFoundException {
    public OrderException(String message) {
        super(message);
    }
}
