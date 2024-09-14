package org.example.service.exception;

public class CoffeeNotFoundException extends RuntimeException {
    public CoffeeNotFoundException(Long id) {
        super(String.format("Coffee with '%d' id is not found!", id));
    }
}
