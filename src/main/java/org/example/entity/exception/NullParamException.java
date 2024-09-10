package org.example.entity.exception;

public class NullParamException extends RuntimeException {
    public NullParamException() {
        super("Parameter cannot be null(!");
    }
    public NullParamException(String paramName) {
        super(String.format("Parameter '%s' cannot be null!", paramName));
    }
}
