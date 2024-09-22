package org.example.service.exception;

public class OrderHasReferencesException extends RuntimeException {
    public OrderHasReferencesException(Long id) {
        super(String.format("Order entity '%d' has references!", id));
    }
}
