package org.example.service.exception;

public class BaristaNotFoundException extends RuntimeException {
    public BaristaNotFoundException(Long id) {
        super(String.format("Barista with '%d' id is not found!", id));
    }
}
