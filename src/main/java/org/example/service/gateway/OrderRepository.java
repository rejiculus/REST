package org.example.service.gateway;

import org.example.entity.Order;

import java.util.List;
import java.util.Optional;

/**
 * Interface to interact with order in db.
 */
public interface OrderRepository {

    /**
     * Find all orders with specific barista's id.
     *
     * @param baristaId barista id.
     * @return list of orders with specified barista.
     */
    List<Order> findByBaristaId(Long baristaId);

    /**
     * Create order in db by specified entity.
     *
     * @param order reference to created object.
     * @return Order object with defined id.
     */
    Order create(Order order);

    /**
     * Update Order in db by specified entity.
     *
     * @param order reference to created object.
     * @return updated order object.
     */
    Order update(Order order);

    /**
     * Delete order by specified id.
     *
     * @param id deleting order's id.
     */
    void delete(Long id);

    /**
     * Find all orders in db.
     *
     * @return list of all orders.
     */
    List<Order> findAll();

    /**
     * Find all order grouped by page and limited.
     *
     * @param page  number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of order in specified page.
     */
    List<Order> findAllByPage(int page, int limit);

    /**
     * Find order by specified id.
     *
     * @param id order's id.
     * @return Optional Order object.
     */
    Optional<Order> findById(Long id);


    /**
     * Find order objects by specified ids.
     *
     * @param idList order id list.
     * @return List of Order objects.
     */
    List<Order> findById(List<Long> idList);

    /**
     * Set default barista to order with specified id.
     *
     * @param orderId updated order's id.
     */
    void setBaristaDefault(Long orderId);

    /**
     * Find all order by specified coffee id.
     *
     * @param id coffee id.
     * @return list of order object's that contains specified coffee.
     */
    List<Order> findByCoffeeId(Long id);
}
