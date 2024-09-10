package org.example.service;

import java.util.List;

/**
 *
 * @param <T>entity type
 * @param <K>entity identifier type
 */
public interface SimpleCrudService<T, K> {
    T create(T entity);
    T update(T entity);
    void delete(K id);
    List<T> findAll();
    T findById(K id);
}
