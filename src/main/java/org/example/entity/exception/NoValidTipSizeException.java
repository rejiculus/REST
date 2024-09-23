package org.example.entity.exception;

/**
 * Thrown when tip size is NaN, Infinite or less than zero.
 */
public class NoValidTipSizeException extends RuntimeException {
    public NoValidTipSizeException(Double tipSize) {
        super(String.format("Tip size must be a double number that more than zero! Your value '%f'", tipSize));
    }
}
