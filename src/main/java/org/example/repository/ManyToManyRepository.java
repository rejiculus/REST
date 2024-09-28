package org.example.repository;

import org.example.db.ConnectionManager;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.exception.DataBaseException;
import org.example.repository.exception.KeyNotPresentException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ManyToManyRepository {
    private final String updatePair;
    private final String deletePair;
    protected ConnectionManager connectionManager;

    /**
     * Constructor based on connectionManager.
     * Has methods to add and delete references between coupled entity.
     *
     * @param connectionManager for processing adding and deleting references between db.
     * @param updatePairsSql    sql query to update reference between coupled entity.
     * @param deletePairsSql    sql query to delete reference between coupled entity.
     * @throws NullParamException when some of params is null.
     */
    protected ManyToManyRepository(ConnectionManager connectionManager, String updatePairsSql, String deletePairsSql) {
        if (connectionManager == null || updatePairsSql == null || deletePairsSql == null)
            throw new NullParamException();

        this.connectionManager = connectionManager;
        updatePair = updatePairsSql;
        deletePair = deletePairsSql;
    }

    /**
     * Add reference between coupled entity.
     *
     * @param firstColumnId  id first entity specified in sql query.
     * @param secondColumnId id second entity specified in sql query.
     * @throws NullParamException     when some of params is null.
     * @throws NoValidIdException     when some of id's in less than zero.
     * @throws KeyNotPresentException when some of specified id's is not present in coupled tables.
     * @throws DataBaseException      another sql exceptions.
     */
    public void addReference(Long firstColumnId, Long secondColumnId) {
        if (firstColumnId == null || secondColumnId == null)
            throw new NullParamException();
        if (firstColumnId < 0)
            throw new NoValidIdException(firstColumnId);
        if (secondColumnId < 0)
            throw new NoValidIdException(secondColumnId);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updatePair)) {
            preparedStatement.setLong(1, firstColumnId);
            preparedStatement.setLong(2, secondColumnId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            if (e.getSQLState().equals("23503"))
                throw new KeyNotPresentException(e.getMessage());
            else
                throw new DataBaseException(e.getMessage());
        }
    }


    /**
     * Add reference between coupled entity.
     *
     * @param firstColumnId  id first entity specified in sql query.
     * @param secondColumnId id second entity specified in sql query.
     * @throws NullParamException when some of params is null.
     * @throws NoValidIdException when some of id's in less than zero.
     * @throws DataBaseException  another sql exceptions.
     */
    public void deleteReference(Long firstColumnId, Long secondColumnId) {
        if (firstColumnId == null || secondColumnId == null)
            throw new NullParamException();
        if (firstColumnId < 0)
            throw new NoValidIdException(firstColumnId);
        if (secondColumnId < 0)
            throw new NoValidIdException(secondColumnId);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deletePair)) {
            preparedStatement.setLong(1, firstColumnId);
            preparedStatement.setLong(2, secondColumnId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
