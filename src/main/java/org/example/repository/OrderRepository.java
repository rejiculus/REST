package org.example.repository;

import org.example.entity.Order;
import org.example.repository.until.OrderCoffeeSQL;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Interface to interact with order in db.
 */
public abstract class OrderRepository extends ManyToManyRepository {
    protected OrderRepository(Connection connection) {
        super(connection, OrderCoffeeSQL.UPDATE_PAIRS.toString(), OrderCoffeeSQL.DELETE_PAIR.toString());
    }

    /**
     * Find all orders with specific barista's id.
     * @param baristaId barista id.
     * @return list of orders with specified barista.
     */
    public abstract List<Order> findByBaristaId(Long baristaId);

    /**
     * Create order in db by specified entity.
     * @param entity reference to created object.
     * @return Order object with defined id.
     */
    public abstract Order create(Order entity);

    /**
     * Update Order in db by specified entity.
     * @param entity reference to created object.
     * @return updated order object.
     */
    public abstract Order update(Order entity);

    /**
     * Delete order by specified id.
     * @param id deleting order's id.
     */
    public abstract void delete(Long id);

    /**
     * Find all orders in db.
     * @return list of all orders.
     */
    public abstract List<Order> findAll();

    /**
     * Find all order grouped by page and limited.
     * @param page  number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of order in specified page.
     */
    public abstract List<Order> findAllByPage(int page, int limit);

    /**
     * Find order by specified id.
     * @param id order's id.
     * @return Optional Order object.
     */
    public abstract Optional<Order> findById(Long id);

    /**
     * Set default barista to order with specified id.
     * @param orderId updated order's id.
     */
    public abstract void setBaristaDefault(Long orderId);

    /**
     * Find all order by specified coffee id.
     * @param id coffee id.
     * @return list of order object's that contains specified coffee.
     */
    public abstract List<Order> findByCoffeeId(Long id);

    /**
     * Delete references between orders and coffees by specified order id.
     * @param orderId id that relations have to be deleted.
     */
    public abstract void deletePairsByOrderId(Long orderId);
}
