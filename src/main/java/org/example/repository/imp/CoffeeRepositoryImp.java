package org.example.repository.imp;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Coffee;
import org.example.entity.exception.CoffeeNotFoundException;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.CoffeeRepository;
import org.example.repository.exception.DataBaseException;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.repository.mapper.CoffeeMapper;
import org.example.repository.until.CoffeeSQL;
import org.example.repository.until.OrderCoffeeSQL;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class CoffeeRepositoryImp extends CoffeeRepository {
    private CoffeeMapper mapper;

    public CoffeeRepositoryImp(ConnectionManager connectionManager, CoffeeMapper mapper) throws SQLException {
        super(connectionManager.getConnection());

        if (mapper == null)
            throw new NullParamException();
        this.mapper = mapper;
    }

    public CoffeeRepositoryImp(Connection connection, CoffeeMapper mapper) {
        super(connection);

        if (mapper == null)
            throw new NullParamException();
        this.mapper = mapper;
    }

    public CoffeeRepositoryImp(ConnectionManager connectionManager) throws SQLException {
        super(connectionManager.getConnection());
        this.mapper = new CoffeeMapper();
    }

    public CoffeeRepositoryImp(Connection connection) {
        super(connection);
        this.mapper = new CoffeeMapper();
    }

    @Override
    public Coffee create(Coffee coffee) {
        if (coffee == null)
            throw new NullParamException();

        Coffee newCoffee = new Coffee(coffee.getName(), coffee.getPrice(), coffee.getOrderList());

        try (PreparedStatement preparedStatement = connection.prepareStatement(CoffeeSQL.CREATE.toString(), Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, newCoffee.getName());
            preparedStatement.setDouble(2, newCoffee.getPrice());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next())
                newCoffee.setId(resultSet.getLong(1));

            return newCoffee;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public Coffee update(Coffee coffee) {
        if (coffee == null)
            throw new NullParamException();

        Coffee newCoffee = new Coffee(coffee.getId(), coffee.getName(), coffee.getPrice(), coffee.getOrderList());

        try (PreparedStatement preparedStatement = connection.prepareStatement(CoffeeSQL.UPDATE.toString())) {
            preparedStatement.setString(1, newCoffee.getName());
            preparedStatement.setDouble(2, newCoffee.getPrice());
            preparedStatement.setLong(3, newCoffee.getId());
            preparedStatement.executeUpdate();

            if (preparedStatement.getUpdateCount() == 0)
                throw new CoffeeNotFoundException(coffee.getId());

            return newCoffee;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        try (PreparedStatement coffeePreparedStatement = connection.prepareStatement(CoffeeSQL.DELETE.toString())) {
            coffeePreparedStatement.setLong(1, id);
            coffeePreparedStatement.executeUpdate();

            if (coffeePreparedStatement.getUpdateCount() <= 0)
                throw new CoffeeNotFoundException(id);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Coffee> findAll() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CoffeeSQL.FIND_ALL.toString())) {
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();

            return mapper.mapToList(resultSet);
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public Optional<Coffee> findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(CoffeeSQL.FIND_BY_ID.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            return mapper.mapToOptional(resultSet);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Coffee> findAllByPage(int page, int limit) {
        if (limit <= 0)
            throw new NoValidLimitException(limit);
        if (page < 0)
            throw new NoValidPageException(page);

        try (PreparedStatement preparedStatement = connection.prepareStatement(CoffeeSQL.FIND_ALL_BY_PAGE.toString())) {
            preparedStatement.setLong(1, (long) page * limit);
            preparedStatement.setLong(2, limit);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();

            return mapper.mapToList(resultSet);
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Coffee> findByOrderId(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.FIND_BY_ORDER_ID.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            List<Long> coffeeIdList = mapper.mapIds(resultSet);
            return coffeeIdList.stream().map(coffeeId -> findById(coffeeId)
                            .orElseThrow(() -> new CoffeeNotFoundException(coffeeId)))
                    .toList();
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void deleteReferencesByCoffeeId(Long coffeeId) {
        if (coffeeId == null)
            throw new NullParamException();
        if (coffeeId < 0)
            throw new NoValidIdException(coffeeId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.DELETE_BY_COFFEE_ID.toString())) {
            preparedStatement.setLong(1, coffeeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }

    }
}
