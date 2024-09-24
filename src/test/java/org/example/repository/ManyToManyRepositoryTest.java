package org.example.repository;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.exception.KeyNotPresentException;
import org.example.repository.imp.BaristaRepositoryImp;
import org.example.repository.imp.CoffeeRepositoryImp;
import org.example.repository.imp.OrderRepositoryImp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManyToManyRepositoryTest {
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

        ConnectionManager connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Connection connection = connectionManager.getConnection();

        baristaRepository = new BaristaRepositoryImp(connection);
        orderRepository = new OrderRepositoryImp(connection);
        coffeeRepository = new CoffeeRepositoryImp(connection);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void addReferenceTest() {
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
    void addReferenceWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.addReference(null, 1L));
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.addReference(1L, null));
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.addReference(null, null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeRepository.addReference(100L, -100L));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeRepository.addReference(-100L, 100L));
        Assertions.assertThrows(KeyNotPresentException.class, () -> coffeeRepository.addReference(1L, 100L));
    }

    @Test
    void deleteReferenceTest() {
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

        coffeeRepository.deleteReference(specificOrder.getId(), specificCoffee1.getId());

        resultCoffeeList = coffeeRepository.findByOrderId(specificOrder.getId());
        assertEquals(List.of(specificCoffee2, specificCoffee3), resultCoffeeList);
    }

    @Test
    void deleteReferenceWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.deleteReference(null, 1L));
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.deleteReference(1L, null));
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.deleteReference(null, null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeRepository.deleteReference(100L, -100L));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeRepository.deleteReference(-100L, 100L));

    }

}