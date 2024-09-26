package org.example.repository.imp;

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
    private final CoffeeMapper mapper;

    public CoffeeRepositoryImp(Connection connection) {
        super(connection);
        this.mapper = new CoffeeMapper();
    }

    /**
     * Create Coffee in db by coffee entity.
     *
     * @param coffee object with coffee type.
     * @return Coffee object with defined id.
     * @throws NullParamException when coffee entity is null.
     * @throws DataBaseException  sql exception.
     */
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

    /**
     * Update coffee entity in db.
     *
     * @param coffee object with coffee type.
     * @return updated Coffee object.
     * @throws NullParamException      when coffee param is null.
     * @throws CoffeeNotFoundException when coffee is not found in db.
     * @throws DataBaseException       sql exception.
     */
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

    /**
     * Delete coffee entity by specified id.
     *
     * @param id deleting coffee's id.
     * @throws NullParamException      when id param is null.
     * @throws NoValidIdException      when id is less than zero.
     * @throws CoffeeNotFoundException when coffee with specified id.
     * @throws DataBaseException       sql exception.
     */
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

    /**
     * Find all coffee in db.
     *
     * @return list of all coffee object from db.
     * @throws DataBaseException sql exception.
     */
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

    /**
     * Find coffee by id.
     *
     * @param id find coffee's id.
     * @return Optional Coffee object.
     * @throws NullParamException when id param is null.
     * @throws NoValidIdException when id is less than zero.
     * @throws DataBaseException  sql exception.
     */
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

    /**
     * Find all coffee object's grouped by page and limited.
     *
     * @param page  number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of all coffee object's form specified page.
     * @throws NoValidLimitException when limit is less than one.
     * @throws NoValidPageException  when page is less than zero.
     * @throws DataBaseException     sql exception.
     */
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

    /**
     * Find all coffee object's coupled with specified order id.
     *
     * @param id order id.
     * @return list of coffee object's
     * @throws NullParamException      when id param is null.
     * @throws NoValidIdException      when id is less than zero.
     * @throws CoffeeNotFoundException when coffee with specified id is not found in db.
     * @throws DataBaseException       sql exception.
     */
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

    /**
     * Delete all references coupled with coffee with specified id.
     *
     * @param coffeeId id that relations have to be deleted.
     * @throws NullParamException when id param is null.
     * @throws NoValidIdException when id is less than zero.
     * @throws DataBaseException  sql exception.
     */
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
