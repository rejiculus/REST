package org.example.servlet.dto;

import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.service.dto.IOrderNoRefDTO;
import org.example.service.exception.BaristaNotFoundException;
import org.example.service.exception.CoffeeNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public record OrderNoRefDTO(Long id,
                            Long baristaId,
                            LocalDateTime created,
                            LocalDateTime completed,
                            Double price,
                            List<Long> coffeeIdList)
        implements IOrderNoRefDTO {

    public OrderNoRefDTO(Order order) {
        this(
                order.getId(),
                order.getBarista().getId(),
                order.getCreated(),
                order.getCompleted(),
                order.getPrice(),
                order.getCoffeeList().stream()
                        .map(Coffee::getId)
                        .toList()
        );
    }
}
