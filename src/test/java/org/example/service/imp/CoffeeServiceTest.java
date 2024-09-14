package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidPriceException;
import org.example.entity.exception.NullParamException;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.service.exception.CoffeeAlreadyExistException;
import org.example.service.exception.CoffeeNotFoundException;
import org.example.servlet.dto.CoffeeNoRefDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoffeeServiceTest {
    static AutoCloseable mocks;
    @Spy
    OrderRepository orderRepository;
    @Spy
    CoffeeRepository coffeeRepository;

    @InjectMocks
    CoffeeService coffeeService;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    public static void close() throws Exception {
        mocks.close();
    }

    @Test
    void findAllTest() {
        List<Coffee> specifiedCoffeeList = new ArrayList<>(List.of(
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class)
        ));

        Mockito.when(coffeeRepository.findAll())
                .thenReturn(specifiedCoffeeList);

        List<Coffee> resultCoffeeList = coffeeService.findAll();

        assertEquals(specifiedCoffeeList, resultCoffeeList);
    }

    @Test
    void findByIdTest() {
        Long specifiedId = 1L;
        Coffee specifiedCoffee = Mockito.mock(Coffee.class);

        Mockito.when(coffeeRepository.findById(specifiedId))
                .thenReturn(Optional.of(specifiedCoffee));

        Coffee resultCoffee = coffeeService.findById(specifiedId);

        assertEquals(specifiedCoffee, resultCoffee);
    }

    @Test
    void findByIdWrongTest() {
        Long specifiedId = 1L;

        Mockito.when(coffeeRepository.findById(specifiedId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CoffeeNotFoundException.class, () -> coffeeService.findById(specifiedId));
        Assertions.assertThrows(NullParamException.class, () -> coffeeService.findById(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeService.findById(-1L));
    }

    @Test
    void createTest() {
        Coffee specifiedCoffee = Mockito.mock(Coffee.class);
        CoffeeNoRefDTO specifiedCoffeeDTO = Mockito.mock(CoffeeNoRefDTO.class);

        Coffee expectedCoffee = new Coffee("Frapuchinno", 99.9);
        expectedCoffee.setId(99L);

        Mockito.when(specifiedCoffeeDTO.toCoffee(orderRepository))
                .thenReturn(specifiedCoffee);
        Mockito.when(coffeeRepository.create(specifiedCoffee))
                .thenReturn(expectedCoffee);


        Coffee resultCoffee = coffeeService.create(specifiedCoffeeDTO);

        assertEquals(expectedCoffee, resultCoffee);
    }

    @Test
    void createWrongTest() {
        Coffee specifiedCoffee = Mockito.mock(Coffee.class);
        CoffeeNoRefDTO coffeeDTONull = Mockito.mock(CoffeeNoRefDTO.class);
        CoffeeNoRefDTO coffeeDTONoValidId = Mockito.mock(CoffeeNoRefDTO.class);
        CoffeeNoRefDTO coffeeDTONoValidName = Mockito.mock(CoffeeNoRefDTO.class);
        CoffeeNoRefDTO coffeeDTONoValidPrice = Mockito.mock(CoffeeNoRefDTO.class);
        CoffeeNoRefDTO coffeeDTOAlreadyExist = Mockito.mock(CoffeeNoRefDTO.class);


        Mockito.when(coffeeDTONull.toCoffee(orderRepository))
                .thenThrow(NullParamException.class);
        Mockito.when(coffeeDTONoValidId.toCoffee(orderRepository))
                .thenThrow(NoValidIdException.class);
        Mockito.when(coffeeDTONoValidName.toCoffee(orderRepository))
                .thenThrow(NoValidNameException.class);
        Mockito.when(coffeeDTONoValidPrice.toCoffee(orderRepository))
                .thenThrow(NoValidPriceException.class);
        Mockito.when(coffeeDTOAlreadyExist.toCoffee(orderRepository))
                .thenReturn(specifiedCoffee);
        Mockito.when(coffeeRepository.create(specifiedCoffee))
                .thenThrow(new CoffeeAlreadyExistException(0L));

        Assertions.assertThrows(NullParamException.class, () -> coffeeService.create(coffeeDTONull));
        Assertions.assertThrows(NullParamException.class, () -> coffeeService.create(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeService.create(coffeeDTONoValidId));
        Assertions.assertThrows(NoValidNameException.class, () -> coffeeService.create(coffeeDTONoValidName));
        Assertions.assertThrows(NoValidPriceException.class, () -> coffeeService.create(coffeeDTONoValidPrice));
        Assertions.assertThrows(CoffeeAlreadyExistException.class, () -> coffeeService.create(coffeeDTOAlreadyExist));
    }

    @Test
    void updateTest() {
        Coffee specifiedCoffee = Mockito.mock(Coffee.class);
        CoffeeNoRefDTO specifiedCoffeeDTO = Mockito.mock(CoffeeNoRefDTO.class);

        Mockito.when(specifiedCoffeeDTO.toCoffee(orderRepository))
                .thenReturn(specifiedCoffee);
        Mockito.when(coffeeRepository.update(specifiedCoffee))
                .thenReturn(specifiedCoffee);

        Coffee resultCoffee = coffeeService.update(specifiedCoffeeDTO);

        assertEquals(specifiedCoffee, resultCoffee);
    }

    @Test
    void updateWrongTest() {
        Coffee specifiedCoffee = Mockito.mock(Coffee.class);
        CoffeeNoRefDTO coffeeDTONull = Mockito.mock(CoffeeNoRefDTO.class);
        CoffeeNoRefDTO coffeeDTONoValidId = Mockito.mock(CoffeeNoRefDTO.class);
        CoffeeNoRefDTO coffeeDTONoValidName = Mockito.mock(CoffeeNoRefDTO.class);
        CoffeeNoRefDTO coffeeDTONoValidPrice = Mockito.mock(CoffeeNoRefDTO.class);
        CoffeeNoRefDTO coffeeDTONotFound = Mockito.mock(CoffeeNoRefDTO.class);


        Mockito.when(coffeeDTONotFound.toCoffee(orderRepository))
                .thenReturn(specifiedCoffee);
        Mockito.when(coffeeDTONull.toCoffee(orderRepository))
                .thenThrow(NullParamException.class);
        Mockito.when(coffeeDTONoValidId.toCoffee(orderRepository))
                .thenThrow(NoValidIdException.class);
        Mockito.when(coffeeDTONoValidName.toCoffee(orderRepository))
                .thenThrow(NoValidNameException.class);
        Mockito.when(coffeeDTONoValidPrice.toCoffee(orderRepository))
                .thenThrow(NoValidPriceException.class);
        Mockito.when(coffeeRepository.update(specifiedCoffee))
                .thenThrow(CoffeeNotFoundException.class);

        Assertions.assertThrows(NullParamException.class, () -> coffeeService.update(coffeeDTONull));
        Assertions.assertThrows(NullParamException.class, () -> coffeeService.update(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeService.update(coffeeDTONoValidId));
        Assertions.assertThrows(NoValidNameException.class, () -> coffeeService.update(coffeeDTONoValidName));
        Assertions.assertThrows(NoValidPriceException.class, () -> coffeeService.update(coffeeDTONoValidPrice));
        Assertions.assertThrows(CoffeeNotFoundException.class, () -> coffeeService.update(coffeeDTONotFound));

    }


    @Test
    void deleteTest() {
        Long specifiedId = 0L;

        Assertions.assertDoesNotThrow(() -> coffeeService.delete(specifiedId));
        Mockito.verify(coffeeRepository).delete(specifiedId);
    }

    @Test
    void deleteWrongTest() {
        //todo check id not found exception
        Assertions.assertThrows(NullParamException.class, () -> coffeeService.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeService.delete(-1L));
    }

}