package org.example.entity.exception;

public class BaristaNotFoundException extends RuntimeException {
    public BaristaNotFoundException(Long id) {
        super(String.format("Barista '%d' is not found!", id));
    }
}
