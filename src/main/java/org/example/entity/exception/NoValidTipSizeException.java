package org.example.entity.exception;

public class NoValidTipSizeException extends RuntimeException {
    public NoValidTipSizeException(Double tipSize) {
        super(String.format("Tip size must be a double number that more than zero! Your value '%f'", tipSize));
    }
}
