package org.example.service.mapper;

import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.BaristaNotFoundException;
import org.example.entity.exception.CoffeeNotFoundException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.service.dto.IOrderCreateDTO;
import org.example.service.dto.IOrderUpdateDTO;

import java.util.List;

public class OrderDtoToOrderMapper {
    private final BaristaRepository baristaRepository;
    private final CoffeeRepository coffeeRepository;

    public OrderDtoToOrderMapper(BaristaRepository baristaRepository, CoffeeRepository coffeeRepository) {
        if (baristaRepository == null || coffeeRepository == null)
            throw new NullParamException();

        this.baristaRepository = baristaRepository;
        this.coffeeRepository = coffeeRepository;
    }

    public Order map(IOrderCreateDTO orderDTO) {
        if (orderDTO == null)
            throw new NullParamException();

        Barista barista = baristaRepository
                .findById(orderDTO.baristaId())
                .orElseThrow(() -> new BaristaNotFoundException(orderDTO.baristaId()));
        List<Coffee> coffees = orderDTO.coffeeIdList().stream()
                .map(coffeeId -> coffeeRepository
                        .findById(coffeeId)
                        .orElseThrow(() -> new CoffeeNotFoundException(coffeeId)))
                .toList();

        return new Order(barista, coffees);
    }

    public Order map(IOrderUpdateDTO orderDTO) {
        if (orderDTO == null)
            throw new NullParamException();

        Barista barista = baristaRepository
                .findById(orderDTO.baristaId())
                .orElseThrow(() -> new BaristaNotFoundException(orderDTO.baristaId()));
        List<Coffee> coffees = orderDTO.coffeeIdList().stream()
                .map(coffeeId -> coffeeRepository
                        .findById(coffeeId)
                        .orElseThrow(() -> new CoffeeNotFoundException(coffeeId)))
                .toList();

        return new Order(orderDTO.id(), barista, coffees, orderDTO.created(), orderDTO.completed(), orderDTO.price());
    }
}
