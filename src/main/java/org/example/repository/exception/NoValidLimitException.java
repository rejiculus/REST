package org.example.repository.exception;

public class NoValidLimitException extends RuntimeException {
    public NoValidLimitException(int limit) {
        super(String.format("Limit can't be less than one! Your limit is '%d'.", limit));
    }
}
