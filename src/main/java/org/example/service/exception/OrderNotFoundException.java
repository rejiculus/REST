package org.example.service.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super(String.format("Order with '%d' id is not found!", id));
    }
}
