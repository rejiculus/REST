package org.example.entity.exception;

/**
 * Thrown when barista entity is not found in db
 */
public class BaristaNotFoundException extends RuntimeException {
    public BaristaNotFoundException(Long id) {
        super(String.format("Barista '%d' is not found!", id));
    }
}
