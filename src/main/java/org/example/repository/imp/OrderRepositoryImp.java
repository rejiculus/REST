package org.example.repository.imp;

import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.entity.exception.OrderNotFoundException;
import org.example.repository.OrderRepository;
import org.example.repository.exception.DataBaseException;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.repository.mapper.OrderMapper;
import org.example.repository.until.OrderCoffeeSQL;
import org.example.repository.until.OrderSQL;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class OrderRepositoryImp extends OrderRepository {
    private final OrderMapper mapper;

    public OrderRepositoryImp(Connection connection) {
        super(connection);
        this.mapper = new OrderMapper(new BaristaRepositoryImp(connection));
    }

    public OrderRepositoryImp(Connection connection, OrderMapper mapper) {
        super(connection);

        if (mapper == null)
            throw new NullParamException();

        this.mapper = mapper;
    }

    @Override
    public Order create(Order order) {
        if (order == null)
            throw new NullParamException();

        Order newOrder = new Order(order.getBarista(), order.getCoffeeList(), order.getCreated(), order.getCompleted(), order.getPrice());

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.CREATE.toString(), Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, newOrder.getBarista().getId());

            if (newOrder.getCreated() == null)
                throw new NullParamException();

            preparedStatement.setTimestamp(2, Timestamp.valueOf(newOrder.getCreated()));


            if (newOrder.getCompleted() != null)
                preparedStatement.setTimestamp(3, Timestamp.valueOf(newOrder.getCompleted()));
            else preparedStatement.setTimestamp(3, null);

            preparedStatement.setDouble(4, newOrder.getPrice());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next())
                newOrder.setId(resultSet.getLong(1));

            return newOrder;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public Order update(Order order) {
        if (order == null)
            throw new NullParamException();

        Order newOrder = new Order(order.getId(), order.getBarista(), order.getCoffeeList(), order.getCreated(), order.getCompleted(), order.getPrice());

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.UPDATE.toString())) {
            preparedStatement.setLong(1, newOrder.getBarista().getId());
            if (newOrder.getCreated() == null)
                throw new NullParamException();

            preparedStatement.setTimestamp(2, Timestamp.valueOf(newOrder.getCreated()));

            if (newOrder.getCompleted() != null)
                preparedStatement.setTimestamp(3, Timestamp.valueOf(newOrder.getCompleted()));
            else preparedStatement.setTimestamp(3, null);

            preparedStatement.setDouble(4, newOrder.getPrice());
            preparedStatement.setLong(5, newOrder.getId());
            preparedStatement.executeUpdate();
            if (preparedStatement.getUpdateCount() == 0)
                throw new OrderNotFoundException(order.getId());

            return newOrder;
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

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.DELETE.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            if (preparedStatement.getUpdateCount() == 0)
                throw new OrderNotFoundException(id);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Order> findAll() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.FIND_ALL.toString())) {
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            return mapper.mapToList(resultSet);
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.FIND_BY_ID.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            return mapper.mapToOptional(resultSet);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Order> findByBaristaId(Long baristaId) {
        if (baristaId == null)
            throw new NullParamException();
        if (baristaId < 0)
            throw new NoValidIdException(baristaId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.FIND_BY_BARISTA.toString())) {
            preparedStatement.setLong(1, baristaId);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();

            return mapper.mapToList(resultSet);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Order> findAllByPage(int page, int limit) {
        if (limit <= 0)
            throw new NoValidLimitException(limit);
        if (page < 0)
            throw new NoValidPageException(page);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.FIND_ALL_BY_PAGE.toString())) {
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
    public void setBaristaDefault(Long orderId) {
        if (orderId == null)
            throw new NullParamException();
        if (orderId < 0)
            throw new NoValidIdException(orderId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.SET_BARISTA_DEFAULT.toString())) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.executeQuery();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Order> findByCoffeeId(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.FIND_BY_COFFEE_ID.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            List<Long> orderIdList = mapper.mapIds(resultSet);
            return orderIdList.stream()
                    .map(orderId -> findById(orderId)
                            .orElseThrow(() -> new OrderNotFoundException(orderId)))
                    .toList();
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void deletePairsByOrderId(Long orderId) {
        if (orderId == null)
            throw new NullParamException();
        if (orderId < 0)
            throw new NoValidIdException(orderId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(OrderCoffeeSQL.DELETE_BY_ORDER_ID.toString())) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
