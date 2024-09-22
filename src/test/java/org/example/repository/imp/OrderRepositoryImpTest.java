package org.example.repository.imp;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.db.DatabaseConfig;
import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.repository.mapper.OrderMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryImpTest {
    static ConnectionManager connectionManager;
    static OrderMapper mapper;
    static OrderRepository orderRepository;
    static BaristaRepository baristaRepository;
    static CoffeeRepository coffeeRepository;

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

        connectionManager = Mockito.spy(new ConnectionManagerImp());
        Connection connection = connectionManager.getConnection();

        mapper = Mockito.spy(new OrderMapper(new BaristaRepositoryImp(connection)));

        baristaRepository = new BaristaRepositoryImp(connection);
        orderRepository = new OrderRepositoryImp(connection, mapper);
        coffeeRepository = new CoffeeRepositoryImp(connection);

    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @Test
    void constructorsTest() {
        Connection connection = Mockito.mock(Connection.class);
        Assertions.assertDoesNotThrow(() -> new OrderRepositoryImp(connectionManager.getConnection(), mapper));
        Assertions.assertThrows(NullParamException.class, () -> new OrderRepositoryImp(null, mapper));
        Assertions.assertThrows(NullParamException.class, () -> new OrderRepositoryImp(connection, null));
        Assertions.assertThrows(NullParamException.class, () -> new OrderRepositoryImp(null, null));
    }

    @Test
    void createTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());

        Order resultOrder = orderRepository.create(order);

        assertNotNull(resultOrder.getId());
        assertNotNull(resultOrder.getCreated());
        assertEquals(0.0, resultOrder.getPrice());
        assertEquals(barista, resultOrder.getBarista());
        assertNull(resultOrder.getCompleted());
        assertEquals(List.of(), resultOrder.getCoffeeList());
    }

    @Test
    void createWrongTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());

        Assertions.assertThrows(NullParamException.class, () -> orderRepository.create(null));
        Assertions.assertThrows(NullParamException.class, () -> orderRepository.create(order));
    }

    @Test
    void updateTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());

        order = orderRepository.create(order);

        order.setCreated(LocalDateTime.now());
        Order resultOrder = orderRepository.update(order);

        assertNotNull(resultOrder.getId());
        assertNotNull(resultOrder.getCreated());
        assertEquals(0.0, resultOrder.getPrice());
        assertEquals(barista, resultOrder.getBarista());
        assertNull(resultOrder.getCompleted());
        assertEquals(List.of(), resultOrder.getCoffeeList());
    }

    @Test
    void updateWrongTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());

        Assertions.assertThrows(NullParamException.class, () -> orderRepository.update(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderRepository.update(order));
    }

    @Test
    void deleteTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());

        Order resultOrder = orderRepository.create(order);

        Assertions.assertDoesNotThrow(() -> orderRepository.delete(resultOrder.getId()));
    }

    @Test
    void deleteWrongTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());

        orderRepository.create(order);

        Assertions.assertThrows(NullParamException.class, () -> orderRepository.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderRepository.delete(-1L));
    }

    @Test
    void findAllTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        orderRepository.create(order);
        orderRepository.create(order);
        orderRepository.create(order);

        List<Order> resultOrderList = orderRepository.findAll();

        assertNotNull(resultOrderList);
        assertTrue(resultOrderList.size() >= 3);
    }

    @Test
    void findByIdTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        Order specifiedOrder = orderRepository.create(order);
        orderRepository.create(order);
        orderRepository.create(order);

        Optional<Order> resultOrder = orderRepository.findById(specifiedOrder.getId());

        assertEquals(Optional.of(specifiedOrder), resultOrder);
    }

    @Test
    void findByIdWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> orderRepository.findById(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderRepository.findById(-1L));
    }

    @Test
    void findByBaristaIdTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        Order specifiedOrder1 = orderRepository.create(order);
        Order specifiedOrder2 = orderRepository.create(order);

        List<Order> resultOrderList = orderRepository.findByBaristaId(barista.getId());

        assertEquals(List.of(specifiedOrder1, specifiedOrder2), resultOrderList);
    }

    @Test
    void findByBaristaIdWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> orderRepository.findByBaristaId(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderRepository.findByBaristaId(-1L));
    }

    @Test
    void findAllByPageTest() {
        Barista barista = new Barista("Name");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        orderRepository.create(order);
        orderRepository.create(order);

        List<Order> resultOrderList = orderRepository.findAllByPage(0, 1);
        assertEquals(1, resultOrderList.size());

        resultOrderList = orderRepository.findAllByPage(1, 1);
        assertEquals(1, resultOrderList.size());

        resultOrderList = orderRepository.findAllByPage(0, 2);
        assertEquals(2, resultOrderList.size());
    }

    @Test
    void findAllByPageWrongTest() {
        Assertions.assertThrows(NoValidPageException.class, () -> orderRepository.findAllByPage(-1, 1));
        Assertions.assertThrows(NoValidLimitException.class, () -> orderRepository.findAllByPage(0, 0));
        Assertions.assertThrows(NoValidLimitException.class, () -> orderRepository.findAllByPage(0, -1));

    }


    @Test
    void findByCoffeeIdTest() {
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

        List<Order> resultCoffeeList = orderRepository.findByCoffeeId(specificCoffee2.getId());

        assertEquals(List.of(specificOrder), resultCoffeeList);

    }

    @Test
    void findByCoffeeIdWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> orderRepository.findByCoffeeId(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderRepository.findByCoffeeId(-1L));
    }

    @Test
    void deleteReferencesByOrderIdTest() {
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

        orderRepository.deletePairsByOrderId(specificOrder2.getId());

        resultCoffeeList = coffeeRepository.findByOrderId(specificOrder.getId());
        assertEquals(List.of(specificCoffee1, specificCoffee2), resultCoffeeList);
        resultCoffeeList = coffeeRepository.findByOrderId(specificOrder2.getId());
        assertEquals(List.of(), resultCoffeeList);

    }

    @Test
    void deleteReferencesByOrderIdWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> orderRepository.deletePairsByOrderId(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderRepository.deletePairsByOrderId(-1L));
    }
}