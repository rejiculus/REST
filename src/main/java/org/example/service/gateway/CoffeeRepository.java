package org.example.service.gateway;

import org.example.entity.Coffee;

import java.util.List;
import java.util.Optional;

/**
 * Interface to interaction with Coffee entity in db.
 */
public interface CoffeeRepository {


    /**
     * Create Coffee in db by coffee entity.
     *
     * @param coffee object with coffee type.
     * @return Coffee object with defined id.
     */
    Coffee create(Coffee coffee);

    /**
     * Update Coffee in db by coffee entity.
     *
     * @param coffee object with coffee type.
     * @return updated coffee entity.
     */
    Coffee update(Coffee coffee);

    /**
     * Delete coffee by specified id.
     *
     * @param id deleting coffee's id.
     */
    void delete(Long id);

    /**
     * Find all coffee's objects form db.
     *
     * @return list of coffee objects
     */
    List<Coffee> findAll();

    /**
     * Find all coffee's grouped by pages and limited.
     *
     * @param page  number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of coffee's objects.
     */
    List<Coffee> findAllByPage(int page, int limit);

    /**
     * Find coffee object in db by specified id.
     *
     * @param id find coffee's id.
     * @return Optional Coffee object.
     */
    Optional<Coffee> findById(Long id);

    /**
     * Find coffee objects in db by specified ids.
     *
     * @param idList find coffee's id.
     * @return List of Coffee objects.
     */
    List<Coffee> findById(List<Long> idList);

    /**
     * Find all coffee's that contains order with specified id.
     *
     * @param id order id.
     * @return list of coffee objects that contains specified order.
     */
    List<Coffee> findByOrderId(Long id);

    /**
     * Delete all references between Orders and coffee's by specified coffee id.
     *
     * @param coffeeId id that relations have to be deleted.
     */
}
