package org.example.service.exception;

/**
 * Thrown when trying to delete Order entity? but it has some references in coffee entity.
 */
public class OrderHasReferencesException extends RuntimeException {
    public OrderHasReferencesException(Long id) {
        super(String.format("Order entity '%d' has references!", id));
    }
}
