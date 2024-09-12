package org.example.service.dto;

import org.example.entity.Order;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IOrderNoRefDTO {
    Long id();

    Long baristaId();

    LocalDateTime created();

    LocalDateTime completed();

    Double price();

    List<Long> coffeeIdList();

    Order toOrder(BaristaRepository baristaRepository, CoffeeRepository coffeeRepository);
}
