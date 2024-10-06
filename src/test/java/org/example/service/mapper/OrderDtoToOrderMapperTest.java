package org.example.service.mapper;

import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.service.dto.IOrderCreateDTO;
import org.example.service.dto.IOrderUpdateDTO;
import org.example.servlet.dto.OrderCreateDTO;
import org.example.servlet.dto.OrderUpdateDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoToOrderMapperTest {
    private static BaristaRepository baristaRepository;
    private static CoffeeRepository coffeeRepository;
    private static OrderDtoToOrderMapper mapper;

    @BeforeEach
    void setUp() {
        baristaRepository = Mockito.mock(BaristaRepository.class);
        coffeeRepository = Mockito.mock(CoffeeRepository.class);
        mapper = new OrderDtoToOrderMapper(baristaRepository, coffeeRepository);
    }

    @Test
    void constructorTest() {
        assertDoesNotThrow(() -> new OrderDtoToOrderMapper(baristaRepository, coffeeRepository));
        assertThrows(NullParamException.class, () -> new OrderDtoToOrderMapper(null, coffeeRepository));
        assertThrows(NullParamException.class, () -> new OrderDtoToOrderMapper(baristaRepository, null));
        assertThrows(NullParamException.class, () -> new OrderDtoToOrderMapper(null, null));
    }

    @Test
    void mapWringTest() {
        Assertions.assertThrows(NullParamException.class, () -> mapper.map((IOrderCreateDTO) null));
        Assertions.assertThrows(NullParamException.class, () -> mapper.map((IOrderUpdateDTO) null));
    }

    @Test
    void mapCreateTest() {
        IOrderCreateDTO specifiedOrderDTO = new OrderCreateDTO(1L, List.of(1L, 2L));
        Barista specifiedBarista = Mockito.mock(Barista.class);
        Coffee specifiedCoffee1 = Mockito.mock(Coffee.class);
        Coffee specifiedCoffee2 = Mockito.mock(Coffee.class);


        Mockito.when(baristaRepository.findById(1L))
                .thenReturn(Optional.of(specifiedBarista));
        Mockito.when(coffeeRepository.findById(List.of(1L, 2L)))
                .thenReturn(List.of(specifiedCoffee1, specifiedCoffee2));

        Order resultOrder = mapper.map(specifiedOrderDTO);

        assertThrows(NoValidIdException.class, resultOrder::getId);
        assertEquals(specifiedBarista, resultOrder.getBarista());
        assertEquals(List.of(specifiedCoffee1, specifiedCoffee2), resultOrder.getCoffeeList());
        assertEquals(0.0, resultOrder.getPrice());
        assertNull(resultOrder.getCreated());
        assertNull(resultOrder.getCompleted());
    }

    @Test
    void mapUpdateTest() {
        LocalDateTime specifiedCreatedTime = Mockito.mock(LocalDateTime.class);
        LocalDateTime specifiedCompletedTime = Mockito.mock(LocalDateTime.class);
        Double specifiedPrice = 100.0;
        IOrderUpdateDTO specifiedOrderDTO = new OrderUpdateDTO(99L, 1L, specifiedCreatedTime, specifiedCompletedTime, specifiedPrice, List.of(1L, 2L));
        Barista specifiedBarista = Mockito.mock(Barista.class);
        Coffee specifiedCoffee1 = Mockito.mock(Coffee.class);
        Coffee specifiedCoffee2 = Mockito.mock(Coffee.class);


        Mockito.when(baristaRepository.findById(1L))
                .thenReturn(Optional.of(specifiedBarista));
        Mockito.when(coffeeRepository.findById(List.of(1L, 2L)))
                .thenReturn(List.of(specifiedCoffee1, specifiedCoffee2));

        Order resultOrder = mapper.map(specifiedOrderDTO);

        assertEquals(99L, resultOrder.getId());
        assertEquals(specifiedBarista, resultOrder.getBarista());
        assertEquals(List.of(specifiedCoffee1, specifiedCoffee2), resultOrder.getCoffeeList());
        assertEquals(specifiedPrice, resultOrder.getPrice());
        assertEquals(specifiedCreatedTime, resultOrder.getCreated());
        assertEquals(specifiedCompletedTime, resultOrder.getCompleted());
    }

}