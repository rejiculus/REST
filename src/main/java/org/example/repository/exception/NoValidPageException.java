package org.example.repository.exception;

/**
 * Thrown when specified number of page is less than zero.
 */
public class NoValidPageException extends RuntimeException {
    public NoValidPageException(int page) {
        super(String.format("Page can't be less than zero! Your page value is '%d'.", page));
    }
}
