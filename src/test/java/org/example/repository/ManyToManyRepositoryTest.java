package org.example.repository;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.service.gateway.BaristaRepository;
import org.example.service.gateway.CoffeeRepository;
import org.example.service.gateway.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManyToManyRepositoryTest {
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

        baristaRepository = new BaristaRepositoryImp(connectionManager);
        orderRepository = new OrderRepositoryImp(connectionManager);
        coffeeRepository = new CoffeeRepositoryImp(connectionManager);
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
        Order order = new Order(barista, List.of(specificCoffee1, specificCoffee2, specificCoffee3));
        order.setCreated(LocalDateTime.now());
        Order specificOrder = orderRepository.create(order);

        List<Coffee> resultCoffeeList = coffeeRepository.findByOrderId(specificOrder.getId());

        assertEquals(List.of(specificCoffee1, specificCoffee2, specificCoffee3), resultCoffeeList);
    }


    @Test
    void deleteReferenceTest() {
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

        coffeeRepository.delete(specificCoffee1.getId());

        resultCoffeeList = coffeeRepository.findByOrderId(specificOrder.getId());
        assertEquals(List.of(specificCoffee2, specificCoffee3), resultCoffeeList);
    }

    @Test
    void deleteReferenceWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> coffeeRepository.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> coffeeRepository.delete(-100L));

    }

}