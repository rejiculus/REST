package org.example.repository.mapper;

import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.repository.BaristaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class OrderMapperTest {
    BaristaRepository baristaRepository = Mockito.mock(BaristaRepository.class);
    OrderMapper orderMapper = Mockito.spy(new OrderMapper(baristaRepository));

    @Test
    void mapTest() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        LocalDateTime specifiedDateTime = LocalDateTime.now();

        Barista specifiedBarista = Mockito.mock(Barista.class);

        Mockito.when(resultSet.findColumn("id"))
                .thenReturn(0);
        Mockito.when(resultSet.findColumn("price"))
                .thenReturn(1);
        Mockito.when(resultSet.findColumn("barista"))
                .thenReturn(2);

        Mockito.when(resultSet.getLong(0))
                .thenReturn(0L);
        Mockito.when(resultSet.getTimestamp("created"))
                .thenReturn(Timestamp.valueOf(specifiedDateTime));
        Mockito.when(resultSet.getTimestamp("completed"))
                .thenReturn(Timestamp.valueOf(specifiedDateTime.plusMinutes(1)));
        Mockito.when(resultSet.getDouble(1))
                .thenReturn(1.0);
        Mockito.when(resultSet.getLong(2))
                .thenReturn(0L);
        Mockito.when(baristaRepository.findById(0L))
                .thenReturn(Optional.of(specifiedBarista));

        Order resultOrder = orderMapper.map(resultSet);

        assertEquals(0L, resultOrder.getId());
        assertEquals(specifiedDateTime, resultOrder.getCreated());
        assertEquals(specifiedDateTime.plusMinutes(1), resultOrder.getCompleted());
        assertEquals(1.0, resultOrder.getPrice());
        assertEquals(specifiedBarista, resultOrder.getBarista());
        assertEquals(List.of(), resultOrder.getCoffeeList());
    }


    @Test
    void mapToListTest() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(resultSet.next())
                .thenReturn(false);
        Mockito.verify(orderMapper, Mockito.times(0)).map(any());
    }
}