package org.example.repository;

import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.exception.DataBaseException;
import org.example.repository.until.OrderCoffeeSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ManyToManyRepository {
    protected Connection connection;

    protected ManyToManyRepository(Connection connection) {
        if (connection == null)
            throw new NullParamException();

        this.connection = connection;
    }

    public void addReference(Long firstColumnId, Long secondColumnId) {
        if (firstColumnId == null || secondColumnId == null)
            throw new NullParamException();
        if (firstColumnId < 0)
            throw new NoValidIdException(firstColumnId);
        if (secondColumnId < 0)
            throw new NoValidIdException(secondColumnId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.UPDATE_PAIRS.toString())) {
            preparedStatement.setLong(1, firstColumnId);
            preparedStatement.setLong(2, secondColumnId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public void deleteReference(Long orderId, Long coffeeId) {
        if (orderId == null || coffeeId == null)
            throw new NullParamException();
        if (orderId < 0)
            throw new NoValidIdException(orderId);
        if (coffeeId < 0)
            throw new NoValidIdException(coffeeId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.DELETE_PAIR.toString())) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.setLong(2, coffeeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
