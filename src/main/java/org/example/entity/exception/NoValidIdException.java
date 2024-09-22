package org.example.entity.exception;

public class NoValidIdException extends RuntimeException {
    public NoValidIdException() {
        super("Id is not specified!");
    }

    public NoValidIdException(Long id) {
        super(String.format("ID can't be less than zero! Current value is '%d'.", id));
    }

}
