package org.example.entity.exception;

public class NullParamException extends RuntimeException {
    public NullParamException() {
        super("Parameter cannot be null!");
    }
}
