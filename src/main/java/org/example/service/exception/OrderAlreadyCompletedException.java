package org.example.service.exception;

import org.example.entity.Order;

public class OrderAlreadyCompletedException extends RuntimeException {
    public OrderAlreadyCompletedException(Order order) {
        super(String.format("Order '%d' is already completed at '%s'!", order.getId(), order.getCompleted()));
    }
}
