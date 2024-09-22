package org.example.repository;

import org.example.entity.Order;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public abstract class OrderRepository extends ManyToManyRepository implements SimpleRepository {
    protected OrderRepository(Connection connection) {
        super(connection);
    }

    public abstract List<Order> findByBaristaId(Long baristaId);

    public abstract Order create(Order entity);

    public abstract Order update(Order entity);

    public abstract void delete(Long id);

    public abstract List<Order> findAll();

    public abstract List<Order> findAllByPage(int page, int limit);

    public abstract Optional<Order> findById(Long id);

    public abstract void setBaristaDefault(Long orderId);

    public abstract List<Order> findByCoffeeId(Long id);

    public abstract void deletePairsByOrderId(Long orderId);
}
