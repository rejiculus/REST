package org.example.service.exception;

/**
 * Thrown when trying to delete Coffee entity, but it's till have references with some Order entities.
 */
public class CoffeeHasReferenceException extends RuntimeException {
    public CoffeeHasReferenceException(Long id) {
        super(String.format("Coffee entity '%d' has references!", id));
    }
}
