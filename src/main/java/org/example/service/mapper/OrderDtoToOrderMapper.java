package org.example.service.mapper;

import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.RepositoryFactory;
import org.example.service.dto.IOrderNoRefDTO;
import org.example.service.exception.BaristaNotFoundException;
import org.example.service.exception.CoffeeNotFoundException;
import org.example.servlet.dto.OrderNoRefDTO;

import java.util.List;

public class OrderDtoToOrderMapper {
    private final BaristaRepository baristaRepository;
    private final CoffeeRepository coffeeRepository;

    public OrderDtoToOrderMapper() {
        baristaRepository = RepositoryFactory.getBaristaRepository();
        coffeeRepository = RepositoryFactory.getCoffeeRepository();
    }

    public OrderDtoToOrderMapper(BaristaRepository baristaRepository, CoffeeRepository coffeeRepository) {
        this.baristaRepository = baristaRepository;
        this.coffeeRepository = coffeeRepository;
    }

    public Order map(IOrderNoRefDTO orderDTO) {

        Barista barista = baristaRepository
                .findById(orderDTO.baristaId())
                .orElseThrow(() -> new BaristaNotFoundException(orderDTO.baristaId()));
        List<Coffee> coffees = orderDTO.coffeeIdList().stream()
                .map(coffeeId -> coffeeRepository
                        .findById(coffeeId)
                        .orElseThrow(() -> new CoffeeNotFoundException(coffeeId)))
                .toList();

        Order order = new Order(barista, coffees);

        if (orderDTO.id() != null)
            order.setId(orderDTO.id());
        if (orderDTO.created() != null)
            order.setCreated(orderDTO.created());
        if (orderDTO.completed() != null)
            order.setCompleted(orderDTO.completed());
        if (orderDTO.price() != null)
            order.setPrice(orderDTO.price());

        return order;
    }
}
