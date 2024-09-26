package org.example.repository.exception;

/**
 * Thrown when one of key in adding reference is not present in its table.
 */
public class KeyNotPresentException extends RuntimeException {
    public KeyNotPresentException(String message) {
        super(String.format("The key is not present in coupled table! %s", message));
    }
}
