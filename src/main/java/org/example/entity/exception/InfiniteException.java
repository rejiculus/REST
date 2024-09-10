package org.example.entity.exception;

public class InfiniteException extends RuntimeException {
    public InfiniteException() {
        super("Param can't be infinite!");
    }
}
