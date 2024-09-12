package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.service.dto.IOrderNoRefDTO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final BaristaRepository baristaRepository;
    private final CoffeeRepository coffeeRepository;

    public OrderService(OrderRepository orderRepository, BaristaRepository baristaRepository, CoffeeRepository coffeeRepository) {
        this.orderRepository = orderRepository;
        this.baristaRepository = baristaRepository;
        this.coffeeRepository = coffeeRepository;
    }

    public Order create(IOrderNoRefDTO orderDTO) {
        Order order = orderDTO.toOrder(baristaRepository, coffeeRepository);
        order.setPrice(order.getCoffeeList()
                .stream()
                .map(Coffee::getPrice)
                .reduce(0.0, Double::sum, Double::sum));
        order.setCreated(LocalDateTime.now());
        return this.orderRepository.create(order);
    }

    public Order update(IOrderNoRefDTO orderDTO) {
        Order order = orderDTO.toOrder(baristaRepository, coffeeRepository);
        return this.orderRepository.update(order);
    }

    public void delete(Long id) {
        this.orderRepository.delete(id);
    }

    public List<Order> findAll() {
        return this.orderRepository.findAll();
    }

    public Order findById(Long id) {
        return this.orderRepository.findById(id);
    }

    public List<Order> getOrderQueue() {
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream()
                .filter(order -> order.getCompleted() == null)
                .sorted(Comparator.comparing(Order::getCreated))
                .toList();
    }

    public Order completeOrder(IOrderNoRefDTO orderDTO) {
        Order order = orderDTO.toOrder(baristaRepository, coffeeRepository);
        order.setCompleted(LocalDateTime.now());
        return this.orderRepository.update(order);
    }
}
