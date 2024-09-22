package org.example.repository.mapper;

import org.example.entity.Barista;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class BaristaMapperTest {
    @Spy
    BaristaMapper baristaMapper = Mockito.spy(new BaristaMapper());

    @Test
    void mapTest() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);


        Mockito.when(resultSet.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSet.findColumn("full_name"))
                .thenReturn(1);
        Mockito.when(resultSet.findColumn("tip_size"))
                .thenReturn(2);

        Mockito.when(resultSet.getLong(0))
                .thenReturn(0L);
        Mockito.when(resultSet.getString(1))
                .thenReturn("John Doe");
        Mockito.when(resultSet.getDouble(2))
                .thenReturn(0.5);

        Barista resultBarista = baristaMapper.map(resultSet);

        assertEquals(0L, resultBarista.getId());
        assertEquals("John Doe", resultBarista.getFullName());
        assertEquals(0.5, resultBarista.getTipSize());
        assertEquals(List.of(), resultBarista.getOrderList());
    }

    @Test
    void mapWrongTest() throws SQLException {
        ResultSet resultSetNullId = Mockito.mock(ResultSet.class);
        ResultSet resultSetNullFullName = Mockito.mock(ResultSet.class);
        ResultSet resultSetWrongFullName = Mockito.mock(ResultSet.class);
        ResultSet resultSetNullTip = Mockito.mock(ResultSet.class);
        ResultSet resultSetWrongTip = Mockito.mock(ResultSet.class);


        Mockito.when(resultSetNullId.findColumn("id"))
                .thenThrow(SQLException.class);
        Mockito.when(resultSetNullFullName.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSetWrongFullName.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSetNullTip.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSetWrongTip.findColumn("id"))
                .thenReturn(0);

        Mockito.when(resultSetNullId.findColumn("full_name"))
                .thenReturn(1);
        Mockito.when(resultSetNullFullName.findColumn("full_name"))
                .thenThrow(SQLException.class);
        Mockito.when(resultSetWrongFullName.findColumn("full_name"))
                .thenReturn(1);
        Mockito.when(resultSetNullTip.findColumn("full_name"))
                .thenReturn(1);
        Mockito.when(resultSetWrongTip.findColumn("full_name"))
                .thenReturn(1);

        Mockito.when(resultSetNullId.findColumn("tip_size"))
                .thenReturn(2);
        Mockito.when(resultSetNullFullName.findColumn("tip_size"))
                .thenReturn(2);
        Mockito.when(resultSetWrongFullName.findColumn("tip_size"))
                .thenReturn(2);
        Mockito.when(resultSetNullTip.findColumn("tip_size"))
                .thenThrow(SQLException.class);
        Mockito.when(resultSetWrongTip.findColumn("tip_size"))
                .thenReturn(2);

        Mockito.when(resultSetNullId.getString(1))
                .thenReturn("John Doe");
        Mockito.when(resultSetNullId.getDouble(2))
                .thenReturn(0.5);

        Mockito.when(resultSetNullFullName.getLong(0))
                .thenReturn(99L);
        Mockito.when(resultSetNullFullName.getDouble(2))
                .thenReturn(0.5);

        Mockito.when(resultSetWrongFullName.getLong(0))
                .thenReturn(99L);
        Mockito.when(resultSetWrongFullName.getString(1))
                .thenReturn("");
        Mockito.when(resultSetWrongFullName.getDouble(2))
                .thenReturn(0.5);

        Mockito.when(resultSetNullTip.getLong(0))
                .thenReturn(99L);
        Mockito.when(resultSetNullTip.getString(1))
                .thenReturn("John Doe");

        Mockito.when(resultSetWrongTip.getLong(0))
                .thenReturn(99L);
        Mockito.when(resultSetWrongTip.getString(1))
                .thenReturn("John Doe");
        Mockito.when(resultSetWrongTip.getDouble(2))
                .thenReturn(-0.1);

        assertThrows(NullParamException.class, () -> baristaMapper.map(resultSetNullId));
        assertThrows(NullParamException.class, () -> baristaMapper.map(resultSetNullFullName));
        assertThrows(NoValidNameException.class, () -> baristaMapper.map(resultSetWrongFullName));
        assertThrows(NullParamException.class, () -> baristaMapper.map(resultSetNullTip));
        assertThrows(NoValidTipSizeException.class, () -> baristaMapper.map(resultSetWrongTip));
    }

    @Test
    void mapToListTest() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(resultSet.next())
                .thenReturn(false);
        Mockito.verify(baristaMapper, Mockito.times(0)).map(any());
    }
}