package org.example.entity.exception;

public class LessZeroException extends RuntimeException {
    public LessZeroException(Double value) {
        super(String.format("Param value can't be less than zero! Current param value '%f'.", value));
    }
}

