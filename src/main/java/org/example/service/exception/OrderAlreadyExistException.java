package org.example.service.exception;

public class OrderAlreadyExistException extends RuntimeException {
    public OrderAlreadyExistException(Long id) {
        super(String.format("Order with '%d' id is already exist!", id));
    }
}
