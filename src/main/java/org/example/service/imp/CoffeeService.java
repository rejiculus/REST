package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.service.dto.ICoffeeNoRefDTO;
import org.example.service.exception.CoffeeNotFoundException;

import java.util.List;

public class CoffeeService {
    private final CoffeeRepository coffeeRepository;
    private final OrderRepository orderRepository;

    public CoffeeService(CoffeeRepository coffeeRepository, OrderRepository orderRepository) {
        this.coffeeRepository = coffeeRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * @param coffeeDTO
     * @return
     * @throws NullParamException      ICoffeeNoRefDTO
     * @throws NoValidIdException      ICoffeeNoRefDTO
     * @throws NoValidNameException    ICoffeeNoRefDTO
     * @throws NoValidTipSizeException ICoffeeNoRefDTO
     */
    public Coffee create(ICoffeeNoRefDTO coffeeDTO) {
        if (coffeeDTO == null)
            throw new NullParamException();

        Coffee coffee = coffeeDTO.toCoffee(orderRepository);
        return this.coffeeRepository.create(coffee);
    }

    /**
     * @param coffeeDTO
     * @return
     * @throws NullParamException      ICoffeeNoRefDTO
     * @throws NoValidIdException      ICoffeeNoRefDTO
     * @throws NoValidNameException    ICoffeeNoRefDTO
     * @throws NoValidTipSizeException ICoffeeNoRefDTO
     */
    public Coffee update(ICoffeeNoRefDTO coffeeDTO) {
        if (coffeeDTO == null)
            throw new NullParamException();

        Coffee coffee = coffeeDTO.toCoffee(orderRepository);
        return this.coffeeRepository.update(coffee);
    }

    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        this.coffeeRepository.delete(id);

    }

    public Coffee findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        return this.coffeeRepository.findById(id)
                .orElseThrow(() -> new CoffeeNotFoundException(id));
    }

    public List<Coffee> findAll() {
        return this.coffeeRepository.findAll();
    }
}
