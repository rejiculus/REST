package org.example.repository.mapper;

import org.example.entity.Coffee;
import org.example.entity.exception.NullParamException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CoffeeMapper implements SimpleMapper<Coffee> {

    /**
     * Mapping result set to Coffee object.
     *
     * @param resultSet result data from sql query.
     * @return Coffee object.
     * @throws NullParamException when some params in result set is not defined.
     */
    @Override
    public Coffee map(ResultSet resultSet) {
        try {
            int idColumn = resultSet.findColumn("id");
            int nameColumn = resultSet.findColumn("name");
            int priceColumn = resultSet.findColumn("price");

            Long id = resultSet.getLong(idColumn);
            String name = resultSet.getString(nameColumn);
            Double price = resultSet.getDouble(priceColumn);

            Coffee coffee = new Coffee(name, price);
            coffee.setId(id);

            coffee.setOrderList(new ArrayList<>());

            return coffee;
        } catch (SQLException e) {
            throw new NullParamException();
        }
    }
}
