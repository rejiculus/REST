package org.example.repository.exception;

/**
 * Thrown when specified limit of presenting entities is less than 1.
 */
public class NoValidLimitException extends RuntimeException {
    public NoValidLimitException(int limit) {
        super(String.format("Limit can't be less than one! Your limit is '%d'.", limit));
    }
}
