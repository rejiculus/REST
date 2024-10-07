package org.example.repository;

import org.example.db.ConnectionManager;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.exception.DataBaseException;
import org.example.repository.exception.KeyNotPresentException;
import org.example.repository.until.OrderCoffeeSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public abstract class ReferredRepository {
    protected final ConnectionManager connectionManager;

    protected ReferredRepository(ConnectionManager connectionManager) {
        if (connectionManager == null)
            throw new NullParamException();

        this.connectionManager = connectionManager;
    }

    /**
     * Delete all references coupled with coffee with specified id.
     *
     * @param coffeeId id that relations have to be deleted.
     * @throws NullParamException when id param is null.
     * @throws NoValidIdException when id is less than zero.
     * @throws DataBaseException  sql exception.
     */
    protected void deleteReferencesByCoffeeId(Long coffeeId) {
        if (coffeeId == null)
            throw new NullParamException();
        if (coffeeId < 0)
            throw new NoValidIdException(coffeeId);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.DELETE_BY_COFFEE_ID.toString())) {
            preparedStatement.setLong(1, coffeeId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }

    }

    protected void deleteReferencesByOrderId(Long orderId) {
        if (orderId == null)
            throw new NullParamException();
        if (orderId < 0)
            throw new NoValidIdException(orderId);


        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.DELETE_BY_ORDER_ID.toString())) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    protected void addReference(Long orderId, Long coffeeId) {

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.UPDATE_PAIRS.toString())) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.setLong(2, coffeeId);
            preparedStatement.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new KeyNotPresentException(e.getMessage());

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    protected void addAllReference(List<Long> orderIdList, Long coffeeId) {

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.UPDATE_PAIRS.toString())) {
            preparedStatement.setLong(2, coffeeId);
            for (Long orderId : orderIdList) {
                preparedStatement.setLong(1, orderId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new KeyNotPresentException(e.getMessage());

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    protected void addAllReference(Long orderId, List<Long> coffeeIdList) {

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.UPDATE_PAIRS.toString())) {
            preparedStatement.setLong(1, orderId);
            for (Long coffeeId : coffeeIdList) {
                preparedStatement.setLong(2, coffeeId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new KeyNotPresentException(e.getMessage());

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    protected void deleteReference(Long coffeeId, Long orderId) {

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.DELETE_PAIR.toString())) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.setLong(2, coffeeId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
