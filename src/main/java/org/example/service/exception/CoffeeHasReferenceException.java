package org.example.service.exception;

public class CoffeeHasReferenceException extends RuntimeException {
    public CoffeeHasReferenceException(Long id) {
        super(String.format("Coffee entity '%d' has references!", id));
    }
}
