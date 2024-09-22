package org.example.repository.imp;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.db.DatabaseConfig;
import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.CoffeeNotFoundException;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.repository.mapper.CoffeeMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.sql.Connection;
import java.sql.SQLException;
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
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine3.20")
            .withCopyFileToContainer(MountableFile.forClasspathResource("DB_script.sql"),
                    "/docker-entrypoint-initdb.d/01-schema.sql");

    @BeforeAll
    static void beforeAll() throws SQLException {
        postgres.start();
        DatabaseConfig.setDbUrl(postgres.getJdbcUrl());
        DatabaseConfig.setUsername(postgres.getUsername());
        DatabaseConfig.setPassword(postgres.getPassword());
        ConnectionManager connectionManager = new ConnectionManagerImp();
        Connection connection = connectionManager.getConnection();
        coffeeRepository = new CoffeeRepositoryImp(connection);
        orderRepository = new OrderRepositoryImp(connection);
        baristaRepository = new BaristaRepositoryImp(connection);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void constructorsTest() {
        ConnectionManager connectionManager = Mockito.spy(new ConnectionManagerImp());
        CoffeeMapper mapper = Mockito.spy(new CoffeeMapper());
        Assertions.assertDoesNotThrow(() -> new CoffeeRepositoryImp(connectionManager, mapper));
        Assertions.assertThrows(NullParamException.class, () -> new CoffeeRepositoryImp(connectionManager, null));
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
        assertThrows(NullParamException.class, () -> coffeeRepository.findById(null));
        assertThrows(NoValidIdException.class, () -> coffeeRepository.findById(-1L));
    }


    @Test
    void findByOrderIdTest() {
        Coffee insertedCoffee = new Coffee("name", 0.0);
        Coffee specificCoffee1 = coffeeRepository.create(insertedCoffee);
        Coffee specificCoffee2 = coffeeRepository.create(insertedCoffee);
        Coffee specificCoffee3 = coffeeRepository.create(insertedCoffee);

        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        Order specificOrder = orderRepository.create(order);

        coffeeRepository.addReference(specificOrder.getId(), specificCoffee1.getId());
        coffeeRepository.addReference(specificOrder.getId(), specificCoffee2.getId());
        coffeeRepository.addReference(specificOrder.getId(), specificCoffee3.getId());

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
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        Order specificOrder = orderRepository.create(order);
        Order specificOrder2 = orderRepository.create(order);

        coffeeRepository.addReference(specificOrder.getId(), specificCoffee1.getId());
        coffeeRepository.addReference(specificOrder.getId(), specificCoffee2.getId());
        coffeeRepository.addReference(specificOrder2.getId(), specificCoffee1.getId());
        coffeeRepository.addReference(specificOrder2.getId(), specificCoffee3.getId());

        List<Coffee> resultCoffeeList = coffeeRepository.findByOrderId(specificOrder.getId());
        assertEquals(List.of(specificCoffee1, specificCoffee2), resultCoffeeList);
        resultCoffeeList = coffeeRepository.findByOrderId(specificOrder2.getId());
        assertEquals(List.of(specificCoffee1, specificCoffee3), resultCoffeeList);

        coffeeRepository.deleteReferencesByCoffeeId(specificCoffee1.getId());

        resultCoffeeList = coffeeRepository.findByOrderId(specificOrder.getId());
        assertEquals(List.of(specificCoffee2), resultCoffeeList);
        resultCoffeeList = coffeeRepository.findByOrderId(specificOrder2.getId());
        assertEquals(List.of(specificCoffee3), resultCoffeeList);

    }

    @Test
    void deleteReferencesByCoffeeIdWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.deleteReferencesByCoffeeId(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeRepository.deleteReferencesByCoffeeId(-1L));
    }


}