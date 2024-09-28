package org.example.service.imp;

import org.example.entity.Coffee;
import org.example.entity.exception.*;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.servlet.dto.CoffeeCreateDTO;
import org.example.servlet.dto.CoffeeUpdateDTO;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoffeeServiceTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withCopyFileToContainer(MountableFile.forClasspathResource("DB_script.sql"),
                    "/docker-entrypoint-initdb.d/01-schema.sql");
    static AutoCloseable mocks;
    @Mock
    CoffeeRepository coffeeRepository;
    @Mock
    OrderRepository orderRepository;

    CoffeeService coffeeService;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        coffeeService = new CoffeeService(
                orderRepository,
                coffeeRepository
        );
    }

    @AfterAll
    public static void close() throws Exception {
        mocks.close();
        postgres.stop();
    }

    @Test
    void constructorsTest() {
        Assertions.assertDoesNotThrow(() -> new CoffeeService(orderRepository, coffeeRepository));
        Assertions.assertThrows(NullParamException.class, () -> new CoffeeService(orderRepository, null));
        Assertions.assertThrows(NullParamException.class, () -> new CoffeeService(null, coffeeRepository));
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
        CoffeeCreateDTO specifiedCoffeeDTO = new CoffeeCreateDTO("name", 0.1);

        Coffee expectedCoffee = new Coffee("Frapuchinno", 99.9);
        expectedCoffee.setId(99L);

        Mockito.when(coffeeRepository.create(Mockito.argThat(coffee -> coffee.getName().equals(specifiedCoffeeDTO.name()))))
                .thenReturn(expectedCoffee);


        Coffee resultCoffee = coffeeService.create(specifiedCoffeeDTO);

        assertEquals(expectedCoffee, resultCoffee);
    }

    @Test
    void createWrongTest() {
        CoffeeCreateDTO coffeeDTONull = new CoffeeCreateDTO(null, 0.1);
        CoffeeCreateDTO coffeeDTONoValidName = new CoffeeCreateDTO("", 0.1);
        CoffeeCreateDTO coffeeDTONoValidPrice = new CoffeeCreateDTO("name", -0.1);


        Assertions.assertThrows(NullParamException.class, () -> coffeeService.create(coffeeDTONull));
        Assertions.assertThrows(NullParamException.class, () -> coffeeService.create(null));
        Assertions.assertThrows(NoValidNameException.class, () -> coffeeService.create(coffeeDTONoValidName));
        Assertions.assertThrows(NoValidPriceException.class, () -> coffeeService.create(coffeeDTONoValidPrice));
    }

    @Test
    void updateTest() {
        Coffee specifiedCoffee = Mockito.mock(Coffee.class);
        CoffeeUpdateDTO specifiedCoffeeDTO = new CoffeeUpdateDTO(0L, "name", 0.1, List.of());

        Mockito.when(coffeeRepository.update(Mockito.argThat(coffee -> coffee.getName().equals(specifiedCoffeeDTO.name()))))
                .thenReturn(specifiedCoffee);

        Coffee resultCoffee = coffeeService.update(specifiedCoffeeDTO);

        assertEquals(specifiedCoffee, resultCoffee);
    }

    @Test
    void updateWrongTest() {
        CoffeeUpdateDTO coffeeDTONull = new CoffeeUpdateDTO(null, "name", 0.1, List.of());
        CoffeeUpdateDTO coffeeDTONoValidId = new CoffeeUpdateDTO(-1L, "name", 0.1, List.of());
        CoffeeUpdateDTO coffeeDTONoValidName = new CoffeeUpdateDTO(0L, "", 0.1, List.of());
        CoffeeUpdateDTO coffeeDTONoValidPrice = new CoffeeUpdateDTO(0L, "name", -0.1, List.of());
        CoffeeUpdateDTO coffeeDTONotFound = new CoffeeUpdateDTO(0L, "name", 0.1, List.of());

        Mockito.when(coffeeRepository.update(Mockito.argThat(coffee -> coffee.getId().equals(0L))))
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
        Assertions.assertThrows(NullParamException.class, () -> coffeeService.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeService.delete(-1L));
    }

    @Test
    void findAllByPageTest() {
        List<Coffee> specifiedCoffeeListPage0 = new ArrayList<>(List.of(
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class)
        ));
        List<Coffee> specifiedCoffeeListPage1 = new ArrayList<>(List.of(
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class),
                Mockito.mock(Coffee.class)
        ));

        Mockito.when(coffeeRepository.findAllByPage(0, 4))
                .thenReturn(specifiedCoffeeListPage0);

        Mockito.when(coffeeRepository.findAllByPage(1, 4))
                .thenReturn(specifiedCoffeeListPage1);

        List<Coffee> resultCoffeeList = coffeeService.findAllByPage(0, 4);
        assertEquals(specifiedCoffeeListPage0, resultCoffeeList);

        resultCoffeeList = coffeeService.findAllByPage(1, 4);
        assertEquals(specifiedCoffeeListPage1, resultCoffeeList);
    }

    @Test
    void findAllByPageWrongTest() {
        Assertions.assertThrows(NoValidPageException.class, () -> coffeeService.findAllByPage(-1, 1));
        Assertions.assertThrows(NoValidLimitException.class, () -> coffeeService.findAllByPage(0, 0));
        Assertions.assertThrows(NoValidLimitException.class, () -> coffeeService.findAllByPage(0, -1));
    }
}