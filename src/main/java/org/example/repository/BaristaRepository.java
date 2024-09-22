package org.example.repository;

import org.example.entity.Barista;

import java.util.List;
import java.util.Optional;

public interface BaristaRepository extends SimpleRepository {
    Barista create(Barista entity);

    Barista update(Barista entity);

    void delete(Long id);

    List<Barista> findAll();

    List<Barista> findAllByPage(int page, int limit);

    Optional<Barista> findById(Long id);
}
