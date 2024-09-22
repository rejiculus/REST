package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.repository.imp.BaristaRepositoryImp;
import org.example.repository.imp.CoffeeRepositoryImp;
import org.example.repository.imp.OrderRepositoryImp;
import org.example.service.dto.IOrderCreateDTO;
import org.example.service.dto.IOrderUpdateDTO;
import org.example.service.exception.OrderAlreadyCompletedException;
import org.example.service.mapper.OrderDtoToOrderMapper;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final CoffeeRepository coffeeRepository;
    private final OrderDtoToOrderMapper mapper;


    public OrderService(BaristaRepository baristaRepository, CoffeeRepository coffeeRepository, OrderRepository orderRepository) {
        if (baristaRepository == null || orderRepository == null || coffeeRepository == null)
            throw new NullParamException();

        this.orderRepository = orderRepository;
        this.coffeeRepository = coffeeRepository;
        this.mapper = new OrderDtoToOrderMapper(baristaRepository, coffeeRepository);
    }

    public OrderService(Connection connection) {
        if (connection == null)
            throw new NullParamException();

        this.orderRepository = new OrderRepositoryImp(connection);
        this.coffeeRepository = new CoffeeRepositoryImp(connection);
        this.mapper = new OrderDtoToOrderMapper(new BaristaRepositoryImp(connection), coffeeRepository);
    }

    /**
     * @param orderDTO
     * @return
     * @throws NullParamException
     * @throws CreatedNotDefinedException
     * @throws NoValidIdException
     * @throws CompletedBeforeCreatedException
     * @throws NoValidTipSizeException
     */
    public Order create(IOrderCreateDTO orderDTO) {
        if (orderDTO == null)
            throw new NullParamException();

        Order order = mapper.map(orderDTO);

        Double price = order.getCoffeeList().stream()
                .map(Coffee::getPrice)
                .reduce(0.0, Double::sum, Double::sum);

        order.setPrice(price * (1.0 + order.getBarista().getTipSize()));
        order.setCreated(LocalDateTime.now());


        return this.orderRepository.create(order);
    }

    /**
     * @param orderDTO
     * @return
     * @throws NullParamException
     * @throws CreatedNotDefinedException
     * @throws NoValidIdException
     * @throws CompletedBeforeCreatedException
     * @throws NoValidTipSizeException
     */
    public Order update(IOrderUpdateDTO orderDTO) {
        if (orderDTO == null)
            throw new NullParamException();

        Order order = mapper.map(orderDTO);

        Double price = order.getCoffeeList().stream()
                .map(Coffee::getPrice)
                .reduce(0.0, Double::sum, Double::sum);
        order.setPrice(price * (1.0 + order.getBarista().getTipSize()));

        order = this.orderRepository.update(order);

        //update order - coffee references
        List<Coffee> expectedCoffeeList = coffeeRepository.findByOrderId(order.getId());
        List<Coffee> actualCoffeeList = order.getCoffeeList();
        List<Coffee> deletedCoffees = new ArrayList<>(expectedCoffeeList);
        List<Coffee> addedCoffees = new ArrayList<>(actualCoffeeList);
        deletedCoffees.removeAll(actualCoffeeList);
        actualCoffeeList.removeAll(expectedCoffeeList);

        for (Coffee coffee : deletedCoffees) {
            orderRepository.deleteReference(order.getId(), coffee.getId());
        }
        for (Coffee coffee : addedCoffees) {
            orderRepository.addReference(order.getId(), coffee.getId());
        }

        return order;
    }

    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        this.orderRepository.delete(id);

        orderRepository.deletePairsByOrderId(id);
    }

    public List<Order> getOrderQueue() {
        List<Order> orderList = this.orderRepository.findAll();
        orderList = orderList.stream()
                .filter(order -> order.getCompleted() == null)
                .sorted(Comparator.comparing(Order::getCreated))
                .toList();
        for (Order order : orderList) {
            order.setCoffeeList(coffeeRepository.findByOrderId(order.getId()));
        }
        return orderList;
    }

    public Order completeOrder(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getCompleted() != null)
            throw new OrderAlreadyCompletedException(order);

        order.setCompleted(LocalDateTime.now());
        order = this.orderRepository.update(order);

        order.setCoffeeList(coffeeRepository.findByOrderId(order.getId()));
        return order;
    }

    public List<Order> findAll() {
        List<Order> orderList = this.orderRepository.findAll();
        for (Order order : orderList) {
            order.setCoffeeList(coffeeRepository.findByOrderId(order.getId()));
        }
        return orderList;
    }

    public Order findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.setCoffeeList(coffeeRepository.findByOrderId(order.getId()));

        return order;
    }

    public List<Order> findAllByPage(int page, int limit) {
        if (page < 0)
            throw new NoValidPageException(page);
        if (limit <= 0)
            throw new NoValidLimitException(limit);

        List<Order> orderList = this.orderRepository.findAllByPage(page, limit);
        for (Order order : orderList) {
            order.setCoffeeList(coffeeRepository.findByOrderId(order.getId()));
        }
        return orderList;
    }

}
