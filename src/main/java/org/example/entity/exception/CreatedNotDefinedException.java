package org.example.entity.exception;

public class CreatedNotDefinedException extends RuntimeException {
    public CreatedNotDefinedException() {
        super("Created time can't be null!");
    }
}
