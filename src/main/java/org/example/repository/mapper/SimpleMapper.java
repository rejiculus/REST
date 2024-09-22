package org.example.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface SimpleMapper<T> {
    T map(ResultSet resultSet) throws SQLException;

    default List<T> mapToList(ResultSet resultSet) throws SQLException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(this.map(resultSet));
        }
        return list;
    }

    default List<Long> mapIds(ResultSet resultSet) throws SQLException {
        List<Long> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getLong(1));
        }
        return list;
    }

    default Optional<T> mapToOptional(ResultSet resultSet) throws SQLException {
        if (resultSet.next())
            return Optional.of(this.map(resultSet));
        else
            return Optional.empty();
    }
}
