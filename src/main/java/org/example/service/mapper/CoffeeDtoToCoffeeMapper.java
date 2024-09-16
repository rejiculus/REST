package org.example.service.mapper;

import org.example.entity.Coffee;
import org.example.repository.OrderRepository;
import org.example.repository.RepositoryFactory;
import org.example.service.dto.ICoffeeNoRefDTO;
import org.example.service.exception.OrderNotFoundException;
import org.example.servlet.dto.CoffeeNoRefDTO;

public class CoffeeDtoToCoffeeMapper {
    private final OrderRepository orderRepository;

    public CoffeeDtoToCoffeeMapper() {
        orderRepository = RepositoryFactory.getOrderRepository();
    }

    public CoffeeDtoToCoffeeMapper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Coffee map(ICoffeeNoRefDTO coffeeDTO) {
        Coffee coffee = new Coffee(coffeeDTO.name(), coffeeDTO.price());

        if (coffeeDTO.id() != null)
            coffee.setId(coffeeDTO.id());
        if (coffeeDTO.orderIdList() != null && !coffeeDTO.orderIdList().isEmpty())
            coffee.setOrderList(coffeeDTO.orderIdList().stream()
                    .map(orderId -> orderRepository.findById(orderId)
                            .orElseThrow(() -> new OrderNotFoundException(orderId)))
                    .toList());

        return coffee;
    }
}
