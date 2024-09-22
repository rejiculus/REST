package org.example.service.mapper;

import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.OrderRepository;
import org.example.service.dto.IBaristaCreateDTO;
import org.example.service.dto.IBaristaUpdateDTO;
import org.example.servlet.dto.BaristaCreateDTO;
import org.example.servlet.dto.BaristaUpdateDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaristaDtoToBaristaMapperTest {
    private static OrderRepository orderRepository;
    private static BaristaDtoToBaristaMapper mapper;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        mapper = new BaristaDtoToBaristaMapper(orderRepository);
    }

    @Test
    void constructorsTest() {
        Assertions.assertDoesNotThrow(() -> new BaristaDtoToBaristaMapper(orderRepository));
        Assertions.assertThrows(NullParamException.class, () -> new BaristaDtoToBaristaMapper(null));
    }

    @Test
    void mapBaristaCreateTest() {
        IBaristaCreateDTO specifiedBaristaDto = new BaristaCreateDTO("John Doe", 0.5);

        Barista resultBarista = mapper.map(specifiedBaristaDto);

        assertEquals("John Doe", resultBarista.getFullName());
        assertEquals(0.5, resultBarista.getTipSize());
        assertEquals(List.of(), resultBarista.getOrderList());
        assertThrows(NoValidIdException.class, () -> resultBarista.getId());
    }

    @Test
    void mapWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> mapper.map((IBaristaCreateDTO) null));
        Assertions.assertThrows(NullParamException.class, () -> mapper.map((IBaristaUpdateDTO) null));
    }

    @Test
    void mapBaristaUpdateTest() {
        IBaristaUpdateDTO specifiedBaristaDto = new BaristaUpdateDTO(0L, "John Doe", 0.5, List.of(1L));
        Order specifiedOrder = Mockito.mock(Order.class);

        Mockito.when(specifiedOrder.getId())
                .thenReturn(1L);
        Mockito.when(orderRepository.findById(1L))
                .thenReturn(Optional.of(specifiedOrder));

        Barista barista = mapper.map(specifiedBaristaDto);

        assertEquals("John Doe", barista.getFullName());
        assertEquals(0.5, barista.getTipSize());
        assertEquals(List.of(specifiedOrder), barista.getOrderList());
        assertEquals(0L, barista.getId());
    }

}