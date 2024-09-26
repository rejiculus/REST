package org.example.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface SimpleMapper<T> {
    /**
     * Mapping result set to object with specified type.
     *
     * @param resultSet result data from sql query.
     * @return Object with specific type.
     */
    T map(ResultSet resultSet);

    /**
     * Mapping result set to list of object's with specified type.
     *
     * @param resultSet result data from sql query.
     * @return List of object with specified type.
     * @throws SQLException
     */
    default List<T> mapToList(ResultSet resultSet) throws SQLException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(this.map(resultSet));
        }
        return list;
    }

    /**
     * Mapping result set with id's to list of id's.
     *
     * @param resultSet result data from sql query.
     * @return List of id.
     * @throws SQLException
     */
    default List<Long> mapIds(ResultSet resultSet) throws SQLException {
        List<Long> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getLong(1));
        }
        return list;
    }

    /**
     * Mapping result set to optional.
     *
     * @param resultSet result data from sql query.
     * @return Optional object: if result's set next is false - optional.empty
     * @throws SQLException
     */
    default Optional<T> mapToOptional(ResultSet resultSet) throws SQLException {
        if (resultSet.next())
            return Optional.of(this.map(resultSet));
        else
            return Optional.empty();
    }
}
