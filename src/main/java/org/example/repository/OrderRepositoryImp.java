package org.example.repository;

import org.example.db.ConnectionManager;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.entity.exception.OrderNotFoundException;
import org.example.repository.exception.DataBaseException;
import org.example.repository.mapper.OrderMapper;
import org.example.repository.until.OrderSQL;
import org.example.repository.until.QueryUntil;
import org.example.service.exception.NoValidLimitException;
import org.example.service.exception.NoValidPageException;
import org.example.service.gateway.OrderRepository;

import java.sql.*;
import java.util.*;

/**
 * Class to interact with order entity in db.
 */
public class OrderRepositoryImp extends ReferredRepository implements OrderRepository {
    private final OrderMapper mapper;

    public OrderRepositoryImp(ConnectionManager connectionManager) {
        super(connectionManager);
        this.mapper = new OrderMapper(new BaristaRepositoryImp(connectionManager));
    }


    /**
     * Create order in db by order object.
     *
     * @param order reference to created object.
     * @return Order object with specified id.
     * @throws NullParamException when 'order' param is null or if order's created field is null.
     * @throws DataBaseException  sql exception.
     */
    @Override
    public Order create(Order order) {
        if (order == null)
            throw new NullParamException();

        Order newOrder = new Order(order.getBarista(), order.getCoffeeList(), order.getCreated(), order.getCompleted(), order.getPrice());

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.CREATE.toString(), Statement.RETURN_GENERATED_KEYS)) {
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

            //add relations
            List<Long> coffeeIdList = newOrder.getCoffeeList().stream()
                    .map(Coffee::getId)
                    .toList();
            addAllReference(newOrder.getId(), coffeeIdList);


            return newOrder;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * Update Order in db by specified entity.
     *
     * @param order reference to created object.
     * @return updated order object.
     * @throws NullParamException     when 'order' param is null or if order's created field is null.
     * @throws OrderNotFoundException when order with specified id is not found in db.
     * @throws DataBaseException      sql exception.
     */
    @Override
    public Order update(Order order) {
        if (order == null)
            throw new NullParamException();

        Order newOrder = new Order(order.getId(), order.getBarista(), order.getCoffeeList(), order.getCreated(), order.getCompleted(), order.getPrice());

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.UPDATE.toString())) {
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

            //update relations
            deleteReferencesByOrderId(newOrder.getId());
            List<Long> coffeeIdList = newOrder.getCoffeeList().stream()
                    .map(Coffee::getId)
                    .toList();
            addAllReference(newOrder.getId(), coffeeIdList);

            return newOrder;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * Delete order with specified id.
     *
     * @param id deleting order's id.
     * @throws NullParamException     when id is null.
     * @throws NoValidIdException     when id is less than zero.
     * @throws OrderNotFoundException when order with specified id is not found in db.
     * @throws DataBaseException      sql exception.
     */
    @Override
    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        deleteReferencesByOrderId(id);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.DELETE.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            if (preparedStatement.getUpdateCount() == 0)
                throw new OrderNotFoundException(id);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * Find all orders in db.
     *
     * @return list of all orders from db.
     * @throws DataBaseException sql exception.
     */
    @Override
    public List<Order> findAll() {
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeQuery(OrderSQL.FIND_ALL.toString());

            ResultSet resultSet = statement.getResultSet();
            return mapper.mapToList(resultSet);
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * Find all order grouped by page and limited.
     *
     * @param page  number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of order in specified page.
     * @throws NoValidLimitException when limit is less than one.
     * @throws NoValidPageException  when page is less than zero.
     * @throws DataBaseException     sql exception.
     */
    @Override
    public List<Order> findAllByPage(int page, int limit) {
        if (limit <= 0)
            throw new NoValidLimitException(limit);
        if (page < 0)
            throw new NoValidPageException(page);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.FIND_ALL_BY_PAGE.toString())) {
            preparedStatement.setLong(1, (long) page * limit);
            preparedStatement.setLong(2, limit);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            return mapper.mapToList(resultSet);
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * Find order with specified id.
     *
     * @param id order's id.
     * @return Optional order object.
     * @throws NullParamException when id is null.
     * @throws NoValidIdException when id is less than zero.
     * @throws DataBaseException  sql exception.
     */
    @Override
    public Optional<Order> findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.FIND_BY_ID.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            return mapper.mapToOptional(resultSet);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Order> findById(List<Long> idList) {
        if (idList == null)
            throw new NullParamException();
        if (idList.isEmpty())
            return new ArrayList<>();


        Map<Long, Order> containedOrderMap = new HashMap<>();
        List<Order> resultOrderList = new ArrayList<>();
        String sql = String.format(OrderSQL.FIND_ALL_BY_ID.toString(), QueryUntil.generatePlaceholders(idList.size()));

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < idList.size(); i++) {
                preparedStatement.setLong(1 + i, idList.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Order order = mapper.map(resultSet);
                containedOrderMap.put(order.getId(), order);
            }

            for (Long id : idList) {
                Order order = containedOrderMap.get(id);
                if (order == null)
                    throw new OrderNotFoundException(id);

                resultOrderList.add(order);
            }

            return resultOrderList;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * Find all orders with specific barista's id.
     *
     * @param baristaId barista id.
     * @return list of orders with specified barista.
     * @throws NullParamException when baristaId is null.
     * @throws NoValidIdException when baristaId is less than zero.
     * @throws DataBaseException  sql exception.
     */
    @Override
    public List<Order> findByBaristaId(Long baristaId) {
        if (baristaId == null)
            throw new NullParamException();
        if (baristaId < 0)
            throw new NoValidIdException(baristaId);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.FIND_BY_BARISTA.toString())) {
            preparedStatement.setLong(1, baristaId);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();

            return mapper.mapToList(resultSet);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * Find all order by specified coffee id.
     *
     * @param id coffee id.
     * @return list of order object's that contains specified coffee.
     * @throws NullParamException     when id is null.
     * @throws NoValidIdException     when id is less than zero.
     * @throws OrderNotFoundException when order with specified id is not found in db.
     * @throws DataBaseException      sql exception.
     */
    @Override
    public List<Order> findByCoffeeId(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.FIND_BY_COFFEE.toString())) {
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

    /**
     * Set default barista to order with specified id.
     *
     * @param orderId updated order's id.
     * @throws NullParamException when orderId is null.
     * @throws NoValidIdException when orderId is less than zero.
     * @throws DataBaseException  sql exception.
     */
    @Override
    public void setBaristaDefault(Long orderId) {
        if (orderId == null)
            throw new NullParamException();
        if (orderId < 0)
            throw new NoValidIdException(orderId);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(OrderSQL.SET_BARISTA_DEFAULT.toString())) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.executeQuery();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
