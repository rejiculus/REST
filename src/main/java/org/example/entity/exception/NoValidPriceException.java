package org.example.entity.exception;

/**
 * Thrown when price is NaN, Infinite of less than zero.
 */
public class NoValidPriceException extends RuntimeException {
    public NoValidPriceException(Double price) {
        super(String.format("Price must be a double number that more than zero! Your value '%f'", price));
    }
}
