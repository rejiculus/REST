package org.example.repository.mapper;

import org.example.entity.Barista;
import org.example.entity.exception.NullParamException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BaristaMapper implements SimpleMapper<Barista> {

    @Override
    public Barista map(ResultSet resultSet) throws SQLException {
        try {
            int idColumn = resultSet.findColumn("id");
            int fullNameColumn = resultSet.findColumn("full_name");
            int tipColumn = resultSet.findColumn("tip_size");

            Long id = resultSet.getLong(idColumn);
            String fullName = resultSet.getString(fullNameColumn);
            Double tipSize = resultSet.getDouble(tipColumn);

            Barista barista = new Barista(fullName, tipSize);
            barista.setId(id);

            barista.setOrderList(new ArrayList<>());//lazy load

            return barista;
        } catch (SQLException e) {
            throw new NullParamException();
        }
    }
}
