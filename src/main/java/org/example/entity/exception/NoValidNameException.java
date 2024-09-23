package org.example.entity.exception;

/**
 * Thrown when name param is empty.
 */
public class NoValidNameException extends RuntimeException {
    public NoValidNameException() {
        super("Name can't be empty!");
    }
}
