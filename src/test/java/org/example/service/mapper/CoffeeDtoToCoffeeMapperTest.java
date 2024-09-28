package org.example.service.mapper;

import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.OrderRepository;
import org.example.service.dto.ICoffeeCreateDTO;
import org.example.service.dto.ICoffeeUpdateDTO;
import org.example.servlet.dto.CoffeeCreateDTO;
import org.example.servlet.dto.CoffeeUpdateDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoffeeDtoToCoffeeMapperTest {
    private static OrderRepository orderRepository;
    private static CoffeeDtoToCoffeeMapper mapper;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        mapper = new CoffeeDtoToCoffeeMapper(orderRepository);
    }


    @Test
    void constructorsTest() {
        Assertions.assertDoesNotThrow(() -> new CoffeeDtoToCoffeeMapper(orderRepository));
        Assertions.assertThrows(NullParamException.class, () -> new CoffeeDtoToCoffeeMapper(null));
    }

    @Test
    void mapWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> mapper.map((ICoffeeCreateDTO) null));
        Assertions.assertThrows(NullParamException.class, () -> mapper.map((ICoffeeUpdateDTO) null));
    }

    @Test
    void mapCreateTest() {
        ICoffeeCreateDTO specifiedCoffeeDTO = new CoffeeCreateDTO("Frappuchinno", 0.1);

        Coffee resultCoffee = mapper.map(specifiedCoffeeDTO);

        assertEquals("Frappuchinno", resultCoffee.getName());
        assertEquals(0.1, resultCoffee.getPrice());
        assertEquals(List.of(), resultCoffee.getOrderList());
        assertThrows(NoValidIdException.class, resultCoffee::getId);
    }

    @Test
    void mapUpdateTest() {
        ICoffeeUpdateDTO specifiedCoffeeDTO = new CoffeeUpdateDTO(99L, "Frappuchinno", 0.1, List.of(1L));
        Order specifiedOrder = Mockito.mock(Order.class);

        Mockito.when(specifiedOrder.getId())
                .thenReturn(1L);
        Mockito.when(orderRepository.findById(1L))
                .thenReturn(Optional.of(specifiedOrder));

        Coffee resultCoffee = mapper.map(specifiedCoffeeDTO);

        assertEquals("Frappuchinno", resultCoffee.getName());
        assertEquals(0.1, resultCoffee.getPrice());
        assertEquals(List.of(specifiedOrder), resultCoffee.getOrderList());
        assertEquals(99L, resultCoffee.getId());
    }

}