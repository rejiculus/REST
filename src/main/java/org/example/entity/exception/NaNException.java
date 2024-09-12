package org.example.entity.exception;

public class NaNException extends RuntimeException {
    public NaNException() {
        super("Param value can't be NaN!");
    }
}
