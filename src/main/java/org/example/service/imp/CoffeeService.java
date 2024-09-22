package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.repository.imp.CoffeeRepositoryImp;
import org.example.repository.imp.OrderRepositoryImp;
import org.example.service.dto.ICoffeeCreateDTO;
import org.example.service.dto.ICoffeeUpdateDTO;
import org.example.service.exception.CoffeeHasReferenceException;
import org.example.service.mapper.CoffeeDtoToCoffeeMapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CoffeeService {
    private final CoffeeRepository coffeeRepository;
    private final OrderRepository orderRepository;

    private final CoffeeDtoToCoffeeMapper mapper;

    public CoffeeService(OrderRepository orderRepository, CoffeeRepository coffeeRepository) {
        if (orderRepository == null || coffeeRepository == null)
            throw new NullParamException();

        this.orderRepository = orderRepository;
        this.coffeeRepository = coffeeRepository;
        this.mapper = new CoffeeDtoToCoffeeMapper(orderRepository);
    }

    public CoffeeService(Connection connection) {
        if (connection == null)
            throw new NullParamException();

        this.orderRepository = new OrderRepositoryImp(connection);
        this.coffeeRepository = new CoffeeRepositoryImp(connection);
        this.mapper = new CoffeeDtoToCoffeeMapper(orderRepository);
    }

    /**
     * @param coffeeDTO
     * @return
     * @throws NullParamException      ICoffeeNoRefDTO
     * @throws NoValidIdException      ICoffeeNoRefDTO
     * @throws NoValidNameException    ICoffeeNoRefDTO
     * @throws NoValidTipSizeException ICoffeeNoRefDTO
     */
    public Coffee create(ICoffeeCreateDTO coffeeDTO) {
        if (coffeeDTO == null)
            throw new NullParamException();

        Coffee coffee = mapper.map(coffeeDTO);
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
    public Coffee update(ICoffeeUpdateDTO coffeeDTO) {
        if (coffeeDTO == null)
            throw new NullParamException();

        Coffee coffee = mapper.map(coffeeDTO);
        coffee = this.coffeeRepository.update(coffee);

        //update order - coffee references
        List<Order> expectedOrderList = orderRepository.findByCoffeeId(coffee.getId());
        List<Order> actualOrderList = coffee.getOrderList();
        List<Order> deletedOrders = new ArrayList<>(expectedOrderList);
        List<Order> addedOrders = new ArrayList<>(actualOrderList);
        deletedOrders.removeAll(actualOrderList);
        actualOrderList.removeAll(expectedOrderList);

        for (Order order : deletedOrders) {
            coffeeRepository.deleteReference(order.getId(), coffee.getId());
        }
        for (Order order : addedOrders) {
            coffeeRepository.addReference(order.getId(), coffee.getId());
        }
        return coffee;
    }

    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        if(!orderRepository.findByCoffeeId(id).isEmpty())
            throw new CoffeeHasReferenceException(id);

        this.coffeeRepository.delete(id);

        coffeeRepository.deleteReferencesByCoffeeId(id);

    }

    public Coffee findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        Coffee coffee = this.coffeeRepository.findById(id)
                .orElseThrow(() -> new CoffeeNotFoundException(id));

        coffee.setOrderList(orderRepository.findByCoffeeId(coffee.getId()));

        return coffee;
    }

    public List<Coffee> findAll() {
        List<Coffee> coffeeList = this.coffeeRepository.findAll();

        for (Coffee coffee : coffeeList) {
            coffee.setOrderList(orderRepository.findByCoffeeId(coffee.getId()));
        }
        return coffeeList;
    }

    public List<Coffee> findAllByPage(int page, int limit) {
        if (page < 0)
            throw new NoValidPageException(page);
        if (limit <= 0)
            throw new NoValidLimitException(limit);

        List<Coffee> coffeeList = this.coffeeRepository.findAllByPage(page, limit);

        for (Coffee coffee : coffeeList) {
            coffee.setOrderList(orderRepository.findByCoffeeId(coffee.getId()));
        }
        return coffeeList;
    }
}
