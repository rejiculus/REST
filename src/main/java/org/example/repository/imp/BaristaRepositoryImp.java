package org.example.repository.imp;

import org.example.entity.Barista;
import org.example.entity.exception.BaristaNotFoundException;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepository;
import org.example.repository.exception.DataBaseException;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.repository.mapper.BaristaMapper;
import org.example.repository.until.BaristaSQL;

import java.sql.*;
import java.util.List;
import java.util.Optional;


public class BaristaRepositoryImp implements BaristaRepository {
    private final Connection connection;
    private final BaristaMapper mapper;


    public BaristaRepositoryImp(Connection connection) {
        this.connection = connection;
        mapper = new BaristaMapper();
    }

    @Override
    public Barista create(Barista barista) {
        if (barista == null)
            throw new NullParamException();

        Barista newBarista = new Barista(barista.getFullName(), barista.getOrderList(), barista.getTipSize());

        try (PreparedStatement preparedStatement = connection.prepareStatement(BaristaSQL.CREATE.toString(), Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, newBarista.getFullName());
            preparedStatement.setDouble(2, newBarista.getTipSize());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next())
                newBarista.setId(resultSet.getLong(1));

            return newBarista;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * @param barista
     * @return
     * @throws NoValidIdException when obj id is not specified (equals -1)
     */
    @Override
    public Barista update(Barista barista) {
        if (barista == null)
            throw new NullParamException();

        Barista newBarista = new Barista(barista.getId(), barista.getFullName(), barista.getOrderList(), barista.getTipSize());

        try (PreparedStatement preparedStatement = connection.prepareStatement(BaristaSQL.UPDATE.toString())) {
            preparedStatement.setString(1, newBarista.getFullName());
            preparedStatement.setDouble(2, newBarista.getTipSize());
            preparedStatement.setLong(3, newBarista.getId());
            preparedStatement.executeUpdate();

            if (preparedStatement.getUpdateCount() == 0)
                throw new BaristaNotFoundException(barista.getId());

            return newBarista;

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

        try (PreparedStatement preparedStatement = connection.prepareStatement(BaristaSQL.DELETE.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            if (preparedStatement.getUpdateCount() <= 0)
                throw new BaristaNotFoundException(id);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Barista> findAll() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(BaristaSQL.FIND_ALL.toString())) {
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            return mapper.mapToList(resultSet);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Barista> findAllByPage(int page, int limit) {
        if (limit <= 0)
            throw new NoValidLimitException(limit);
        if (page < 0)
            throw new NoValidPageException(page);

        try (PreparedStatement preparedStatement = connection.prepareStatement(BaristaSQL.FIND_ALL_BY_PAGE.toString())) {
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
    public Optional<Barista> findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(BaristaSQL.FIND_BY_ID.toString())) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            return mapper.mapToOptional(resultSet);
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
