package org.example.service.implementation;

import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.repository.exception.KeyNotPresentException;
import org.example.service.IOrderService;
import org.example.service.dto.IOrderCreateDTO;
import org.example.service.dto.IOrderUpdateDTO;
import org.example.service.exception.NoValidLimitException;
import org.example.service.exception.NoValidPageException;
import org.example.service.exception.OrderAlreadyCompletedException;
import org.example.service.exception.OrderHasReferencesException;
import org.example.service.gateway.BaristaRepository;
import org.example.service.gateway.CoffeeRepository;
import org.example.service.gateway.OrderRepository;
import org.example.service.mapper.OrderDtoToOrderMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class OrderService implements IOrderService {
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

    /**
     * Create 'order' in db by IOrderCreateDTO.
     *
     * @param orderDTO object with IOrderCreateDTO type.
     * @return Order object.
     * @throws NullParamException     when coffeeDTO is null or it's fields is null.
     * @throws KeyNotPresentException from addReference, when some coffee in coffeeList is not found.
     */
    @Override
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
     * Update 'order' in db by IOrderUpdateDTO.
     *
     * @param orderDTO object with IOrderUpdateDTO type.
     * @return updated Order object.
     * @throws NullParamException              when coffeeDTO is null or it's fields is null.
     * @throws NoValidIdException              form mapper, when coffeeDTO's id is less than zero.
     * @throws CreatedNotDefinedException      from mapper, when completed field is specified but created field is not.
     * @throws CompletedBeforeCreatedException from mapper, when completed time is before created time.
     * @throws NoValidTipSizeException         from mapper, when coffeeDTO's price is NaN, Infinite or less than zero.
     * @throws KeyNotPresentException          from addReference, when some coffee in coffeeList is not found.
     */
    @Override
    public Order update(IOrderUpdateDTO orderDTO) {
        if (orderDTO == null)
            throw new NullParamException();

        Order order = mapper.map(orderDTO);

        Double price = order.getCoffeeList().stream()
                .map(Coffee::getPrice)
                .reduce(0.0, Double::sum, Double::sum);
        order.setPrice(price * (1.0 + order.getBarista().getTipSize()));

        return this.orderRepository.update(order);
    }

    /**
     * Delete 'order' by specified id.
     *
     * @param id deleting order's id.
     * @throws NullParamException          when coffeeDTO is null or it's fields is null.
     * @throws NoValidIdException          form mapper, when coffeeDTO's id is less than zero.
     * @throws OrderHasReferencesException when order with specific id has references with some coffee's.
     * @throws OrderNotFoundException      when order with specific id is not found in db.
     */
    @Override
    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        if (!coffeeRepository.findByOrderId(id).isEmpty())
            throw new OrderHasReferencesException(id);

        this.orderRepository.delete(id);
    }

    /**
     * Get 'order' queue. Oldest created, but not completed
     * order - first, youngest - last.
     *
     * @return list of filtered and sorted orders.
     */
    @Override
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

    /**
     * Complete 'order' with specified 'id'.
     * Specifying 'completed' field in 'order'.
     *
     * @param id completing order's id.
     * @return completed order.
     * @throws NullParamException             when coffeeDTO is null or it's fields is null.
     * @throws NoValidIdException             form mapper, when coffeeDTO's id is less than zero.
     * @throws OrderNotFoundException         when order with specific id is not found in db.
     * @throws OrderAlreadyCompletedException when order already has completed time.
     */
    @Override
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

    /**
     * Find all 'order' in db.
     *
     * @return list of all 'order' objects
     */
    @Override
    public List<Order> findAll() {
        List<Order> orderList = this.orderRepository.findAll();
        for (Order order : orderList) {
            order.setCoffeeList(coffeeRepository.findByOrderId(order.getId()));
        }
        return orderList;
    }

    /**
     * Find 'order' by specified id.
     *
     * @param id the desired order "id".
     * @return Order object with specified id.
     * @throws NullParamException     when coffeeDTO is null or it's fields is null.
     * @throws NoValidIdException     form mapper, when coffeeDTO's id is less than zero.
     * @throws OrderNotFoundException when order with specific id is not found in db.
     */
    @Override
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

    /**
     * Find all 'order' grouping by pages and limited.
     *
     * @param page  number of representing page. Can't be less than zero.
     * @param limit number maximum represented objects.
     * @return list of object from specified page. Maximum number object in list equals limit.
     * @throws NoValidPageException  when page is less than zero.
     * @throws NoValidLimitException when limit is less than one.
     */
    @Override
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
