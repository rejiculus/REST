package org.example.repository.imp;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.CoffeeNotFoundException;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepositoryImp;
import org.example.repository.CoffeeRepositoryImp;
import org.example.repository.OrderRepositoryImp;
import org.example.service.exception.NoValidLimitException;
import org.example.service.exception.NoValidPageException;
import org.example.service.gateway.BaristaRepository;
import org.example.service.gateway.CoffeeRepository;
import org.example.service.gateway.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class CoffeeRepositoryImpTest {
    private static CoffeeRepository coffeeRepository;
    private static OrderRepository orderRepository;
    private static BaristaRepository baristaRepository;
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withInitScript("DB_script.sql");

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        ConnectionManager connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        coffeeRepository = new CoffeeRepositoryImp(connectionManager);
        orderRepository = new OrderRepositoryImp(connectionManager);
        baristaRepository = new BaristaRepositoryImp(connectionManager);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void constructorsTest() {
        ConnectionManager connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Assertions.assertDoesNotThrow(() -> new CoffeeRepositoryImp(connectionManager));
        Assertions.assertThrows(NullParamException.class, () -> new CoffeeRepositoryImp(null));
    }

    @Test
    void createTest() {
        Coffee insertCoffee = new Coffee("name", 0.0);

        Coffee resultCoffee = coffeeRepository.create(insertCoffee);

        assertNotNull(resultCoffee.getId());
        assertEquals("name", resultCoffee.getName());
        assertEquals(0.0, resultCoffee.getPrice());
        assertEquals(List.of(), resultCoffee.getOrderList());
    }

    @Test
    void createWrongTest() {
        assertThrows(NullParamException.class, () -> coffeeRepository.create(null));
    }

    @Test
    void updateTest() {
        Coffee coffee = new Coffee("Name", 0.0);

        coffee = coffeeRepository.create(coffee);

        coffee.setPrice(1.9);

        Coffee resultCoffee = coffeeRepository.update(coffee);

        assertEquals("Name", resultCoffee.getName());
        assertEquals(1.9, resultCoffee.getPrice());
    }

    @Test
    void updateWrongTest() {
        Coffee coffee = new Coffee("Name", 0.0);

        assertThrows(NoValidIdException.class, () -> coffeeRepository.update(coffee));
        assertThrows(NullParamException.class, () -> coffeeRepository.update(null));
    }

    @Test
    void deleteTest() {
        Coffee coffee = new Coffee("Name", 0.0);

        coffee = coffeeRepository.create(coffee);

        coffeeRepository.delete(coffee.getId());

        assertEquals(Optional.empty(), coffeeRepository.findById(coffee.getId()));
    }

    @Test
    void deleteWrongTest() {
        assertThrows(NullParamException.class, () -> coffeeRepository.delete(null));
        assertThrows(NoValidIdException.class, () -> coffeeRepository.delete(-1L));
        assertThrows(CoffeeNotFoundException.class, () -> coffeeRepository.delete(99L));
    }

    @Test
    void findAllTest() {
        Coffee insertedCoffee = new Coffee("name", 0.0);
        coffeeRepository.create(insertedCoffee);
        coffeeRepository.create(insertedCoffee);
        coffeeRepository.create(insertedCoffee);

        List<Coffee> resultCoffeeList = coffeeRepository.findAll();

        assertNotNull(resultCoffeeList);
        assertTrue(resultCoffeeList.size() >= 3);
    }

    @Test
    void findAllByPageTest() {
        Coffee insertedCoffee = new Coffee("name", 0.0);
        coffeeRepository.create(insertedCoffee);
        coffeeRepository.create(insertedCoffee);
        coffeeRepository.create(insertedCoffee);

        List<Coffee> resultCoffeeList = coffeeRepository.findAllByPage(0, 3);

        assertNotNull(resultCoffeeList);
        assertEquals(3, resultCoffeeList.size());
    }

    @Test
    void findAllByPageWrongTest() {
        Coffee insertedCoffee = new Coffee("name", 0.0);
        coffeeRepository.create(insertedCoffee);
        coffeeRepository.create(insertedCoffee);
        coffeeRepository.create(insertedCoffee);

        assertThrows(NoValidPageException.class, () -> coffeeRepository.findAllByPage(-1, 3));
        assertThrows(NoValidLimitException.class, () -> coffeeRepository.findAllByPage(0, 0));
        assertThrows(NoValidLimitException.class, () -> coffeeRepository.findAllByPage(0, -1));

    }

    @Test
    void findByIdTest() {
        Coffee expextedCoffee = new Coffee("Name", 0.0);

        expextedCoffee = coffeeRepository.create(expextedCoffee);

        assertEquals(Optional.of(expextedCoffee), coffeeRepository.findById(expextedCoffee.getId()));
    }

    @Test
    void findByIdWrongTest() {
        assertThrows(NullParamException.class, () -> coffeeRepository.findById((Long) null));
        assertThrows(NoValidIdException.class, () -> coffeeRepository.findById(-1L));
    }

    @Test
    void findAllByIdTest() {
        Coffee expextedCoffee1 = new Coffee("Name", 0.0);
        Coffee expextedCoffee2 = new Coffee("Name", 0.0);

        expextedCoffee1 = coffeeRepository.create(expextedCoffee1);
        expextedCoffee2 = coffeeRepository.create(expextedCoffee2);


        List<Coffee> resultCoffeeList = coffeeRepository.findById(List.of(expextedCoffee1.getId(), expextedCoffee2.getId()));

        assertEquals(List.of(expextedCoffee1, expextedCoffee2), resultCoffeeList);
    }

    @Test
    void findAllByIdTestSame() {
        Coffee expextedCoffee1 = new Coffee("Name", 0.0);

        expextedCoffee1 = coffeeRepository.create(expextedCoffee1);


        List<Coffee> resultCoffeeList = coffeeRepository.findById(List.of(expextedCoffee1.getId(), expextedCoffee1.getId()));

        assertEquals(List.of(expextedCoffee1, expextedCoffee1), resultCoffeeList);
    }

    @Test
    void findAllByIdWrongTest() {
        List<Long> notFoundIdList = List.of(1L, 99L, 138L);

        assertThrows(NullParamException.class, () -> coffeeRepository.findById((List<Long>) null));
        assertThrows(CoffeeNotFoundException.class, () -> coffeeRepository.findById(notFoundIdList));
    }


    @Test
    void findByOrderIdTest() {
        Coffee insertedCoffee = new Coffee("name", 0.0);
        Coffee specificCoffee1 = coffeeRepository.create(insertedCoffee);
        Coffee specificCoffee2 = coffeeRepository.create(insertedCoffee);
        Coffee specificCoffee3 = coffeeRepository.create(insertedCoffee);

        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of(specificCoffee1, specificCoffee2, specificCoffee3));
        order.setCreated(LocalDateTime.now());
        Order specificOrder = orderRepository.create(order);

        List<Coffee> resultCoffeeList = coffeeRepository.findByOrderId(specificOrder.getId());

        assertEquals(List.of(specificCoffee1, specificCoffee2, specificCoffee3), resultCoffeeList);

    }

    @Test
    void findByOrderIdWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.findByOrderId(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeRepository.findByOrderId(-1L));
    }

    @Test
    void deleteReferencesByCoffeeIdTest() {
        Coffee insertedCoffee = new Coffee("name", 0.0);
        Coffee specificCoffee1 = coffeeRepository.create(insertedCoffee);
        Coffee specificCoffee2 = coffeeRepository.create(insertedCoffee);
        Coffee specificCoffee3 = coffeeRepository.create(insertedCoffee);

        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order1 = new Order(barista, List.of(specificCoffee2, specificCoffee3));
        Order order2 = new Order(barista, List.of(specificCoffee1, specificCoffee2));
        order1.setCreated(LocalDateTime.now());
        order2.setCreated(LocalDateTime.now());
        Order specificOrder1 = orderRepository.create(order1);
        Order specificOrder2 = orderRepository.create(order2);

        coffeeRepository.delete(specificCoffee1.getId());

        List<Coffee> resultCoffeeList1 = coffeeRepository.findByOrderId(specificOrder1.getId());
        List<Coffee> resultCoffeeList2 = coffeeRepository.findByOrderId(specificOrder2.getId());
        assertEquals(List.of(specificCoffee2, specificCoffee3), resultCoffeeList1);
        assertEquals(List.of(specificCoffee2), resultCoffeeList2);

    }

    @Test
    void deleteReferencesByCoffeeIdWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeRepository.delete(-1L));
    }


}