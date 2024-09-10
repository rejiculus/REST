package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.repository.OrderRepository;
import org.example.service.SimpleCrudService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService implements SimpleCrudService<Order, Long> {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(Order order) {
        order.setPrice(order.getCoffeeList()
                        .stream()
                        .map(Coffee::getPrice)
                        .reduce(0.0, Double::sum, Double::sum));
        order.setCreated(LocalDateTime.now());
        return this.orderRepository.create(order);
    }

    @Override
    public Order update(Order order) {
        return this.orderRepository.update(order);
    }

    @Override
    public void delete(Long id) {
        this.orderRepository.delete(id);

    }

    @Override
    public List<Order> findAll() {
        return this.orderRepository.findAll();
    }

    @Override
    public Order findById(Long id) {
        return this.orderRepository.findById(id);
    }

    public List<Order> getOrderQueue(){
       List<Order> orders = this.orderRepository.findAll();
       return orders.stream()
               .filter(order-> order.getCompleted() == null)
               .sorted(Comparator.comparing(Order::getCreated))
               .collect(Collectors.toList());
    }

    public Order completeOrder(Order order){
        order.setCompleted(LocalDateTime.now());
        return this.orderRepository.update(order);
    }
}
