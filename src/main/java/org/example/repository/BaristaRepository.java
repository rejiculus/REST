package org.example.repository;

import org.example.entity.Barista;

import java.util.List;
import java.util.Optional;

/**
 * Interface to interaction with barista in db.
 */
public interface BaristaRepository {
    /**
     * Create barista in db by barista entity.
     *
     * @param entity object with Barista type.
     * @return Barista object with defined id.
     */
    Barista create(Barista entity);

    /**
     * Update barista in db by barista entity.
     *
     * @param entity object with Barista type.
     * @return updated Barista object.
     */
    Barista update(Barista entity);

    /**
     * Delete barista with specified id.
     *
     * @param id deleting barista's id.
     */
    void delete(Long id);

    /**
     * Find all baristas from db.
     *
     * @return List of barista objects.
     */
    List<Barista> findAll();

    /**
     * Find all baristas from db grouping by pages with limit.
     *
     * @param page  number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of barista's objects
     */
    List<Barista> findAllByPage(int page, int limit);

    /**
     * Found barista from db by id.
     *
     * @param id barista's id.
     * @return Barista's object.
     */
    Optional<Barista> findById(Long id);
}
