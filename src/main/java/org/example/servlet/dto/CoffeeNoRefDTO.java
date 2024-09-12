package org.example.servlet.dto;

import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.repository.OrderRepository;
import org.example.service.dto.ICoffeeNoRefDTO;

import java.util.List;

public record CoffeeNoRefDTO(Long id,
                             String name,
                             Double price,
                             List<Long> orderIdList)
        implements ICoffeeNoRefDTO {

    public CoffeeNoRefDTO(Coffee coffee) {
        this(
                coffee.getId(),
                coffee.getName(),
                coffee.getPrice(),
                coffee.getOrderList().stream()
                        .map(Order::getId)
                        .toList()
        );
    }

    @Override
    public Coffee toCoffee(OrderRepository orderRepository) {
        return new Coffee(
                id,
                name,
                price,
                orderIdList.stream()
                        .map(orderRepository::findById)
                        .toList()
        );
    }
}
