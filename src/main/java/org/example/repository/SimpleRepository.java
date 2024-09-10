package org.example.repository;

import java.util.List;

public interface SimpleRepository<T, K> {
    T create(T entity);
    T update(T entity);
    void delete(K id);
    List<T> findAll();
    T findById(K id);
}
