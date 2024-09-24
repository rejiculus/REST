package org.example.repository.mapper;

import org.example.entity.Barista;
import org.example.entity.exception.NullParamException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Mapper form result set to Barista object.
 */
public class BaristaMapper implements SimpleMapper<Barista> {

    /**
     * Mapping result set ot Barista object.
     * Set to order list - empty list!
     * @param resultSet result data form sql query.
     * @return Barista object.
     * @throws NullParamException when required params is not defined in result set.
     */
    @Override
    public Barista map(ResultSet resultSet) {
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
