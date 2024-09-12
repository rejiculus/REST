package org.example.service.dto;

import org.example.entity.Coffee;
import org.example.repository.OrderRepository;

import java.util.List;

public interface ICoffeeNoRefDTO {
    Long id();

    String name();

    Double price();

    List<Long> orderIdList();

    Coffee toCoffee(OrderRepository orderRepository);
}
