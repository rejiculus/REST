package org.example.repository.exception;

public class KeyNotPresentException extends RuntimeException {
    public KeyNotPresentException(String message) {
        super(String.format("The key is not present in couped table! %s", message));
    }
}
