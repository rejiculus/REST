package org.example.repository.imp;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryImpTest {
    static ConnectionManager connectionManager;
    static OrderRepository orderRepository;
    static BaristaRepository baristaRepository;
    static CoffeeRepository coffeeRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withInitScript("DB_script.sql");


    @BeforeAll
    static void beforeAll() {
        postgres.start();

        connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        baristaRepository = new BaristaRepositoryImp(connectionManager);
        orderRepository = new OrderRepositoryImp(connectionManager);
        coffeeRepository = new CoffeeRepositoryImp(connectionManager);

    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @Test
    void constructorsTest() {
        Assertions.assertDoesNotThrow(() -> new OrderRepositoryImp(connectionManager));
        Assertions.assertThrows(NullParamException.class, () -> new OrderRepositoryImp(null));
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
        Assertions.assertThrows(NullParamException.class, () -> orderRepository.findById((Long) null));
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
        Order order = new Order(barista, List.of(specificCoffee1, specificCoffee2, specificCoffee3));
        order.setCreated(LocalDateTime.now());
        Order specificOrder = orderRepository.create(order);

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
        Order order1 = new Order(barista, List.of(specificCoffee1, specificCoffee3));
        Order order2 = new Order(barista, List.of(specificCoffee2, specificCoffee3));
        order1.setCreated(LocalDateTime.now());
        order2.setCreated(LocalDateTime.now());
        Order specificOrder1 = orderRepository.create(order1);
        Order specificOrder2 = orderRepository.create(order2);

        orderRepository.delete(specificOrder2.getId());

        List<Coffee> resultCoffeeList1 = coffeeRepository.findByOrderId(specificOrder1.getId());
        List<Coffee> resultCoffeeList2 = coffeeRepository.findByOrderId(specificOrder2.getId());
        assertEquals(List.of(specificCoffee1, specificCoffee3), resultCoffeeList1);
        assertEquals(List.of(), resultCoffeeList2);

    }

    @Test
    void deleteReferencesByOrderIdWrongTest() {
        Assertions.assertThrows(NullParamException.class, () -> orderRepository.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderRepository.delete(-1L));
    }
}