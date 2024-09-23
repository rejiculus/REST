package org.example.service.mapper;

import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.BaristaNotFoundException;
import org.example.entity.exception.CoffeeNotFoundException;
import org.example.entity.exception.CreatedNotDefinedException;
import org.example.entity.exception.CompletedBeforeCreatedException;
import org.example.entity.exception.NoValidPriceException;
import org.example.entity.exception.NullParamException;
import org.example.entity.exception.NoValidIdException;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.service.dto.IOrderCreateDTO;
import org.example.service.dto.IOrderUpdateDTO;

import java.util.List;

/**
 * Mapper from IOrderCreateDTO and IOrderUpdateDTO to Order.
 * Required to specify baristaRepository and coffeeRepository in constructor.
 */
public class OrderDtoToOrderMapper {
    private final BaristaRepository baristaRepository;
    private final CoffeeRepository coffeeRepository;

    public OrderDtoToOrderMapper(BaristaRepository baristaRepository, CoffeeRepository coffeeRepository) {
        if (baristaRepository == null || coffeeRepository == null)
            throw new NullParamException();

        this.baristaRepository = baristaRepository;
        this.coffeeRepository = coffeeRepository;
    }

    /**
     * Mapping IOrderCreateDTO to Order.
     * @param orderDTO object with IOrderCreateDTO type.
     * @return Order object, with not specified id!
     * @throws NullParamException when orderDTO or fields of orderDTO is null.
     * @throws BaristaNotFoundException when barista from orderDTO is not found in db.
     * @throws CoffeeNotFoundException when coffee from orderDTO is not found in db.
     */
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

    /**
     * Mapping IOrderUpdateDTO to Order.
     * @param orderDTO object with IOrderCreateDTO type.
     * @return Order object.
     * @throws NullParamException when orderDTO or fields of orderDTO is null.
     * @throws BaristaNotFoundException when barista from orderDTO is not found in db.
     * @throws CoffeeNotFoundException when coffee from orderDTO is not found in db.
     * @throws NoValidIdException from order entity, when id in orderDTO is less than zero.
     * @throws CreatedNotDefinedException from order entity, when complete time is specified by created time is not.
     * @throws CompletedBeforeCreatedException from order entity, when completed time is before than create time.
     * @throws NoValidPriceException from order entity, when price is NaN, Infinite or less than zero.
     */
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
