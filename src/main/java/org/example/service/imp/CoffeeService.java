package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.service.dto.ICoffeeNoRefDTO;

import java.util.List;

public class CoffeeService {
    private final CoffeeRepository coffeeRepository;
    private final OrderRepository orderRepository;

    public CoffeeService(CoffeeRepository coffeeRepository, OrderRepository orderRepository) {
        this.coffeeRepository = coffeeRepository;
        this.orderRepository = orderRepository;
    }

    public Coffee create(ICoffeeNoRefDTO coffeeDTO) {
        Coffee coffee = coffeeDTO.toCoffee(orderRepository);
        return this.coffeeRepository.create(coffee);
    }

    public Coffee update(ICoffeeNoRefDTO coffeeDTO) {
        Coffee coffee = coffeeDTO.toCoffee(orderRepository);
        return this.coffeeRepository.update(coffee);
    }

    public void delete(Long id) {
        this.coffeeRepository.delete(id);

    }

    public Coffee findById(Long id) {
        return this.coffeeRepository.findById(id);
    }

    public List<Coffee> findAll() {
        return this.coffeeRepository.findAll();
    }
}
