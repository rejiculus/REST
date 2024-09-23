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
import org.example.service.ICoffeeService;
import org.example.service.dto.ICoffeeCreateDTO;
import org.example.service.dto.ICoffeeUpdateDTO;
import org.example.service.exception.CoffeeHasReferenceException;
import org.example.service.mapper.CoffeeDtoToCoffeeMapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to processing coffee entity.
 */
public class CoffeeService implements ICoffeeService {
    private final CoffeeRepository coffeeRepository;
    private final OrderRepository orderRepository;

    private final CoffeeDtoToCoffeeMapper mapper;

    /**
     * Constructor based on repositories.
     * Create mapper by orderRepository.
     * @param orderRepository repository to interact with orders in db.
     * @param coffeeRepository repository to interact with coffee in db.
     * @throws NullParamException when orderRepository of coffeeRepository is null.
     */
    public CoffeeService(OrderRepository orderRepository, CoffeeRepository coffeeRepository) {
        if (orderRepository == null || coffeeRepository == null)
            throw new NullParamException();

        this.orderRepository = orderRepository;
        this.coffeeRepository = coffeeRepository;
        this.mapper = new CoffeeDtoToCoffeeMapper(orderRepository);
    }

    /**
     * Constructor based on connection.
     * Create orderRepository and coffeeRepository by connection.
     * Create mapper by orderRepository.
     * @param connection specified connection with db.
     * @throws NullParamException when connection is null.
     */
    public CoffeeService(Connection connection) {
        if (connection == null)
            throw new NullParamException();

        this.orderRepository = new OrderRepositoryImp(connection);
        this.coffeeRepository = new CoffeeRepositoryImp(connection);
        this.mapper = new CoffeeDtoToCoffeeMapper(orderRepository);
    }

    /**
     * Creating coffee by ICoffeeCreateDTO.
     * @param coffeeDTO object with ICoffeeCreateDTO type.
     * @return Coffee object.
     * @throws NullParamException when coffeeDTO is null or it's fields is null.
     * @throws NoValidNameException when coffeeDTO's name is empty.
     * @throws NoValidPriceException when coffeeDTO's price is NaN, Infinite or less than zero.
     */
    @Override
    public Coffee create(ICoffeeCreateDTO coffeeDTO) {
        if (coffeeDTO == null)
            throw new NullParamException();

        Coffee coffee = mapper.map(coffeeDTO);
        return this.coffeeRepository.create(coffee);
    }

    /**
     * Updating coffee by ICoffeeUpdateDTO.
     * Deleting references that has in db but hasn't in coffeeDTO.
     * Add references that hasn't in db but has in coffeeDTO.
     * @param coffeeDTO object with ICoffeeUpdateDTO type.
     * @return Coffee object.
     * @throws NullParamException when coffeeDTO is null or some of it fields is null.
     * @throws NoValidIdException form mapper, when coffeeDTO's id is less than zero.
     * @throws NoValidNameException form mapper, when coffeeDTO's name is empty.
     * @throws NoValidPriceException from mapper, when coffeeDTO's price is NaN, Infinite or less than zero.
     * @throws CoffeeNotFoundException when coffee with this id is not found.
     * @throws OrderNotFoundException when order for coffee's orderList is not found.
     */
    @Override
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

    /**
     * Delete coffee with specified id.
     * @param id deleting coffee id.
     * @throws NullParamException when id is null.
     * @throws NoValidIdException when id is less than zero.
     * @throws CoffeeNotFoundException when coffee with specific id is not found.
     * @throws CoffeeHasReferenceException when coffee whit specific id has references with some orders.
     */
    @Override
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

    /**
     * Find coffee by specified id.
     * @param id finding coffee's id.
     * @return Coffee order with specified id.
     * @throws NullParamException when id param is null.
     * @throws NoValidIdException when id less than zero.
     * @throws CoffeeNotFoundException when coffee with specified id is not found.
     */
    @Override
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

    /**
     * Find all coffees.
     * @return all coffee from db.
     */
    @Override
    public List<Coffee> findAll() {
        List<Coffee> coffeeList = this.coffeeRepository.findAll();

        for (Coffee coffee : coffeeList) {
            coffee.setOrderList(orderRepository.findByCoffeeId(coffee.getId()));
        }
        return coffeeList;
    }

    /**
     * Find all coffee grouping by pages and limited.
     * @param page number of representing page. Can't be less than zero.
     * @param limit number maximum represented objects.
     * @return list of object from specified page. Maximum number object in list equals limit.
     * @throws NoValidPageException when page is less than zero.
     * @throws NoValidLimitException when limit is less than one.
     */
    @Override
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
