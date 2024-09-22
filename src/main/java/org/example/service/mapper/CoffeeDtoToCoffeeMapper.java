package org.example.service.mapper;

import org.example.entity.Coffee;
import org.example.entity.exception.NullParamException;
import org.example.entity.exception.OrderNotFoundException;
import org.example.repository.OrderRepository;
import org.example.service.dto.ICoffeeCreateDTO;
import org.example.service.dto.ICoffeeUpdateDTO;

public class CoffeeDtoToCoffeeMapper {
    private final OrderRepository orderRepository;

    public CoffeeDtoToCoffeeMapper(OrderRepository orderRepository) {
        if (orderRepository == null)
            throw new NullParamException();

        this.orderRepository = orderRepository;
    }

    public Coffee map(ICoffeeCreateDTO coffeeDTO) {
        if (coffeeDTO == null)
            throw new NullParamException();

        return new Coffee(coffeeDTO.name(), coffeeDTO.price());
    }

    public Coffee map(ICoffeeUpdateDTO coffeeDTO) {
        if (coffeeDTO == null)
            throw new NullParamException();

        return new Coffee(
                coffeeDTO.id(),
                coffeeDTO.name(),
                coffeeDTO.price(),
                coffeeDTO.orderIdList()
                        .stream()
                        .map(orderId -> orderRepository.findById(orderId)
                                .orElseThrow(() -> new OrderNotFoundException(orderId)))
                        .toList());
    }
}
