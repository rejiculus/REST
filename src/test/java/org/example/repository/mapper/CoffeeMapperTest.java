package org.example.repository.mapper;

import org.example.entity.Coffee;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidPriceException;
import org.example.entity.exception.NullParamException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class CoffeeMapperTest {
    CoffeeMapper coffeeMapper = Mockito.spy(new CoffeeMapper());

    @Test
    void mapTest() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(resultSet.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSet.findColumn("name"))
                .thenReturn(1);
        Mockito.when(resultSet.findColumn("price"))
                .thenReturn(2);


        Mockito.when(resultSet.getLong(0))
                .thenReturn(0L);
        Mockito.when(resultSet.getString(1))
                .thenReturn("John Doe");
        Mockito.when(resultSet.getDouble(2))
                .thenReturn(0.5);

        Coffee resultCoffee = coffeeMapper.map(resultSet);

        assertEquals(0L, resultCoffee.getId());
        assertEquals("John Doe", resultCoffee.getName());
        assertEquals(0.5, resultCoffee.getPrice());
        assertEquals(List.of(), resultCoffee.getOrderList());
    }

    @Test
    void mapWrongTest() throws SQLException {
        ResultSet resultSetNullId = Mockito.mock(ResultSet.class);
        ResultSet resultSetNullName = Mockito.mock(ResultSet.class);
        ResultSet resultSetWrongName = Mockito.mock(ResultSet.class);
        ResultSet resultSetNullPrice = Mockito.mock(ResultSet.class);
        ResultSet resultSetWrongPrice = Mockito.mock(ResultSet.class);


        Mockito.when(resultSetNullId.findColumn("id"))
                .thenThrow(SQLException.class);
        Mockito.when(resultSetNullName.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSetWrongName.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSetNullPrice.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSetWrongPrice.findColumn("id"))
                .thenReturn(0);

        Mockito.when(resultSetNullId.findColumn("name"))
                .thenReturn(1);
        Mockito.when(resultSetNullName.findColumn("name"))
                .thenThrow(SQLException.class);
        Mockito.when(resultSetWrongName.findColumn("name"))
                .thenReturn(1);
        Mockito.when(resultSetNullPrice.findColumn("name"))
                .thenReturn(1);
        Mockito.when(resultSetWrongPrice.findColumn("name"))
                .thenReturn(1);

        Mockito.when(resultSetNullId.findColumn("price"))
                .thenReturn(2);
        Mockito.when(resultSetNullName.findColumn("price"))
                .thenReturn(2);
        Mockito.when(resultSetWrongName.findColumn("price"))
                .thenReturn(2);
        Mockito.when(resultSetNullPrice.findColumn("price"))
                .thenThrow(SQLException.class);
        Mockito.when(resultSetWrongPrice.findColumn("price"))
                .thenReturn(2);

        Mockito.when(resultSetNullId.getString(1))
                .thenReturn("John Doe");
        Mockito.when(resultSetNullId.getDouble(2))
                .thenReturn(0.5);

        Mockito.when(resultSetNullName.getLong(0))
                .thenReturn(99L);
        Mockito.when(resultSetNullName.getDouble(2))
                .thenReturn(0.5);

        Mockito.when(resultSetWrongName.getLong(0))
                .thenReturn(99L);
        Mockito.when(resultSetWrongName.getString(1))
                .thenReturn("");
        Mockito.when(resultSetWrongName.getDouble(2))
                .thenReturn(0.5);

        Mockito.when(resultSetNullPrice.getLong(0))
                .thenReturn(99L);
        Mockito.when(resultSetNullPrice.getString(1))
                .thenReturn("John Doe");

        Mockito.when(resultSetWrongPrice.getLong(0))
                .thenReturn(99L);
        Mockito.when(resultSetWrongPrice.getString(1))
                .thenReturn("John Doe");
        Mockito.when(resultSetWrongPrice.getDouble(2))
                .thenReturn(-0.1);

        assertThrows(NullParamException.class, () -> coffeeMapper.map(resultSetNullId));
        assertThrows(NullParamException.class, () -> coffeeMapper.map(resultSetNullName));
        assertThrows(NoValidNameException.class, () -> coffeeMapper.map(resultSetWrongName));
        assertThrows(NullParamException.class, () -> coffeeMapper.map(resultSetNullPrice));
        assertThrows(NoValidPriceException.class, () -> coffeeMapper.map(resultSetWrongPrice));
    }

    @Test
    void mapToListTest() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(resultSet.next())
                .thenReturn(false);
        Mockito.verify(coffeeMapper, Mockito.times(0)).map(any());
    }
}