package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.repository.CoffeeRepository;
import org.example.service.SimpleCrudService;

import java.util.List;

public class CoffeeService implements SimpleCrudService<Coffee, Long> {
    private final CoffeeRepository coffeeRepository;

    public CoffeeService(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    @Override
    public Coffee create(Coffee coffee) {
        return this.coffeeRepository.create(coffee);
    }

    @Override
    public Coffee update(Coffee coffee) {
        return this.coffeeRepository.update(coffee);
    }

    @Override
    public void delete(Long id) {
        this.coffeeRepository.delete(id);

    }

    @Override
    public Coffee findById(Long id) {
        return this.coffeeRepository.findById(id);
    }

    @Override
    public List<Coffee> findAll() {
        return this.coffeeRepository.findAll();
    }
}
