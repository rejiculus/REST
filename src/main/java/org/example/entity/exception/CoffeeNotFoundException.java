package org.example.entity.exception;

public class CoffeeNotFoundException extends RuntimeException {
    public CoffeeNotFoundException(Long id) {
        super(String.format("Coffee '%d' is not found!", id));
    }
}
