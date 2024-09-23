package org.example.entity.exception;

/**
 * Thrown when Coffee entity is not found in db
 */
public class CoffeeNotFoundException extends RuntimeException {
    public CoffeeNotFoundException(Long id) {
        super(String.format("Coffee '%d' is not found!", id));
    }
}
