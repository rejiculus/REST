package org.example.repository;

import org.example.entity.Coffee;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public abstract class CoffeeRepository extends ManyToManyRepository implements SimpleRepository {
    protected CoffeeRepository(Connection connection) {
        super(connection);
    }

    public abstract Coffee create(Coffee entity);

    public abstract Coffee update(Coffee entity);

    public abstract void delete(Long id);

    public abstract List<Coffee> findAll();

    public abstract List<Coffee> findAllByPage(int page, int limit);

    public abstract Optional<Coffee> findById(Long id);

    public abstract List<Coffee> findByOrderId(Long id);

    public abstract void deleteReferencesByCoffeeId(Long coffeeId);
}
