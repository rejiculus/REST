package org.example.repository;

import org.example.entity.Coffee;
import org.example.repository.until.OrderCoffeeSQL;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Interface to interaction with Coffee entity in db.
 */
public abstract class CoffeeRepository extends ManyToManyRepository {
    protected CoffeeRepository(Connection connection) {
        super(connection, OrderCoffeeSQL.UPDATE_PAIRS.toString(), OrderCoffeeSQL.DELETE_PAIR.toString());
    }

    /**
     * Create Coffee in db by coffee entity.
     *
     * @param coffee object with coffee type.
     * @return Coffee object with defined id.
     */
    public abstract Coffee create(Coffee coffee);

    /**
     * Update Coffee in db by coffee entity.
     *
     * @param coffee object with coffee type.
     * @return updated coffee entity.
     */
    public abstract Coffee update(Coffee coffee);

    /**
     * Delete coffee by specified id.
     *
     * @param id deleting coffee's id.
     */
    public abstract void delete(Long id);

    /**
     * Find all coffee's objects form db.
     *
     * @return list of coffee objects
     */
    public abstract List<Coffee> findAll();

    /**
     * Find all coffee's grouped by pages and limited.
     *
     * @param page  number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of coffee's objects.
     */
    public abstract List<Coffee> findAllByPage(int page, int limit);

    /**
     * Find coffee object in db by specified id.
     *
     * @param id find coffee's id.
     * @return Optional Coffee object.
     */
    public abstract Optional<Coffee> findById(Long id);

    /**
     * Find all coffee's that contains order with specified id.
     *
     * @param id order id.
     * @return list of coffee objects that contains specified order.
     */
    public abstract List<Coffee> findByOrderId(Long id);

    /**
     * Delete all references between Orders and coffee's by specified coffee id.
     *
     * @param coffeeId id that relations have to be deleted.
     */
    public abstract void deleteReferencesByCoffeeId(Long coffeeId);
}
