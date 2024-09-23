package org.example.entity.exception;

/**
 * Thrown when Order entity is not found in db.
 */
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super(String.format("Order '%d' is not found!", id));
    }
}
