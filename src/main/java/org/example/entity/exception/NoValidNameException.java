package org.example.entity.exception;

public class NoValidNameException extends RuntimeException {
    public NoValidNameException() {
        super("Name can't be empty!");
    }
}
