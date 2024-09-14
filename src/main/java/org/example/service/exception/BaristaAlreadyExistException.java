package org.example.service.exception;

public class BaristaAlreadyExistException extends RuntimeException {
    public BaristaAlreadyExistException(Long id) {
        super(String.format("Barista with '%d' id is already exist!", id));
    }
}
