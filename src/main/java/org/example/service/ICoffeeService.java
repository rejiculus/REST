package org.example.service;

import org.example.entity.Coffee;
import org.example.service.dto.ICoffeeCreateDTO;
import org.example.service.dto.ICoffeeUpdateDTO;

import java.util.List;

/**
 * Interface to interacting with coffee's in db.
 */
public interface ICoffeeService {

    /**
     * Creating coffee by ICoffeeCreateDTO.
     *
     * @param coffeeDTO object with ICoffeeCreateDTO type.
     * @return Coffee object.
     */
    Coffee create(ICoffeeCreateDTO coffeeDTO);

    /**
     * Updating coffee by ICoffeeUpdateDTO.
     * Deleting references that has in db but hasn't in coffeeDTO.
     * Add references that hasn't in db but has in coffeeDTO.
     *
     * @param coffeeDTO object with ICoffeeUpdateDTO type.
     * @return Coffee object.
     */
    Coffee update(ICoffeeUpdateDTO coffeeDTO);

    /**
     * Delete coffee with specified id.
     *
     * @param id deleting coffee id.
     */
    void delete(Long id);

    /**
     * Find coffee by specified id.
     *
     * @param id finding coffee's id.
     * @return Coffee order with specified id.
     */
    Coffee findById(Long id);

    /**
     * Find all coffees.
     *
     * @return all coffee from db.
     */
    List<Coffee> findAll();

    /**
     * Find all coffee grouping by pages and limited.
     *
     * @param page  number of representing page. Can't be less than zero.
     * @param limit number maximum represented objects.
     * @return list of object from specified page. Maximum number object in list equals limit.
     */
    List<Coffee> findAllByPage(int page, int limit);
}
