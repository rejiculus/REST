package org.example.db.exception;

public class PropertyNotPresentException extends RuntimeException {
    public PropertyNotPresentException(String message) {
        super(String.format("Property '%s' must be specified in configuration file!", message));
    }
}
