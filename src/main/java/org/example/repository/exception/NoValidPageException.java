package org.example.repository.exception;

public class NoValidPageException extends RuntimeException {
    public NoValidPageException(int page) {
        super(String.format("Page can't be less than zero! Your page value is '%d'.", page));
    }
}
