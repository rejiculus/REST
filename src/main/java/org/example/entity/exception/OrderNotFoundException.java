package org.example.entity.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super(String.format("Order '%d' is not found!", id));
    }
}
