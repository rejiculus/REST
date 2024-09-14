package org.example.service.imp;

import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.service.exception.OrderAlreadyCompletedException;
import org.example.service.exception.OrderAlreadyExistException;
import org.example.service.exception.OrderNotFoundException;
import org.example.servlet.dto.OrderNoRefDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    static AutoCloseable mocks;
    @Spy
    BaristaRepository baristaRepository;
    @Spy
    OrderRepository orderRepository;
    @Spy
    CoffeeRepository coffeeRepository;

    @InjectMocks
    OrderService orderService;

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
        List<Order> specifiedOrderList = new ArrayList<>(List.of(
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class)
        ));

        Mockito.when(orderRepository.findAll())
                .thenReturn(specifiedOrderList);

        List<Order> resultOrderList = orderService.findAll();

        assertEquals(specifiedOrderList, resultOrderList);
    }

    @Test
    void findByIdTest() {
        Long specifiedId = 99L;
        Order specifiedOrder = Mockito.mock(Order.class);

        Mockito.when(orderRepository.findById(specifiedId))
                .thenReturn(Optional.of(specifiedOrder));

        Order resultOrder = orderService.findById(specifiedId);

        assertEquals(specifiedOrder, resultOrder);
    }

    @Test
    void findByIdWrongTest() {
        Long specifiedId = 99L;

        Mockito.when(orderRepository.findById(specifiedId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.findById(specifiedId));
        Assertions.assertThrows(NullParamException.class, () -> orderService.findById(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderService.findById(-1L));
    }

    //queue
    @Test
    void getOrderQueue() {
        List<Order> specifiedOrderList = new ArrayList<>(List.of(
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>())),
                Mockito.spy(new Order(Mockito.mock(Barista.class), new ArrayList<>()))
        ));
        for (int i = 0; i < specifiedOrderList.size(); i++) {
            Order order = specifiedOrderList.get(i);
            order.setCreated(LocalDateTime.now());
            if (i % 2 == 0)
                order.setCompleted(LocalDateTime.now().plusMinutes(i * 3+1));

            specifiedOrderList.set(i, order);
        }
        List<Order> expectedOrderList = specifiedOrderList.stream()
                .filter(order -> order.getCompleted() == null)
                .sorted(Comparator.comparing(Order::getCreated))
                .toList();

        Mockito.when(orderRepository.findAll())
                .thenReturn(specifiedOrderList);

        List<Order> resultOrderList = orderService.getOrderQueue();


        assertEquals(expectedOrderList, resultOrderList);
    }

    @Test
    void getOrder2Queue() {
        List<Order> specifiedOrderList = new ArrayList<>(List.of(
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>()),
                new Order(Mockito.mock(Barista.class), new ArrayList<>())
        ));
        for (int i = 0; i < specifiedOrderList.size(); i++) {
            Order order = specifiedOrderList.get(i);
            order.setCreated(LocalDateTime.now());
            order.setCompleted(LocalDateTime.now().plusMinutes(i * 3 + 1));

            specifiedOrderList.set(i, order);
        }
        List<Order> expectedOrderList = specifiedOrderList.stream()
                .filter(order -> order.getCompleted() == null)
                .sorted(Comparator.comparing(Order::getCreated))
                .toList();

        Mockito.when(orderRepository.findAll())
                .thenReturn(specifiedOrderList);

        List<Order> resultOrderList = orderService.getOrderQueue();


        assertEquals(expectedOrderList, resultOrderList);
    }

    //create
    @Test
    void createTest() {
        Order specifiedOrder = Mockito.mock(Order.class);
        OrderNoRefDTO specifiedOrderDTO = Mockito.mock(OrderNoRefDTO.class);

        Order expectedOrder = new Order(Mockito.mock(Barista.class), new ArrayList<>());
        expectedOrder.setId(99L);

        Mockito.when(specifiedOrderDTO.toOrder(baristaRepository, coffeeRepository))
                .thenReturn(specifiedOrder);
        Mockito.when(orderRepository.create(specifiedOrder))
                .thenReturn(expectedOrder);

        Order resultOrder = orderService.create(specifiedOrderDTO);

        assertEquals(expectedOrder, resultOrder);
    }

    @Test
    void createWrongTest() {
        Order specifiedOrder = Mockito.mock(Order.class);
        OrderNoRefDTO orderDTONull = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTONoValidId = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTOCreateNotDef = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTOBeforeComplete = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTONoValidPrice = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTOExist = Mockito.mock(OrderNoRefDTO.class);


        Mockito.when(orderDTONull.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(NullParamException.class);
        Mockito.when(orderDTONoValidId.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(NoValidIdException.class);
        Mockito.when(orderDTOCreateNotDef.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(CreatedNotDefinedException.class);
        Mockito.when(orderDTOBeforeComplete.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(CompletedBeforeCreatedException.class);
        Mockito.when(orderDTONoValidPrice.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(NoValidPriceException.class);
        Mockito.when(orderDTOExist.toOrder(baristaRepository, coffeeRepository))
                .thenReturn(specifiedOrder);
        Mockito.when(orderRepository.create(specifiedOrder))
                .thenThrow(new OrderAlreadyExistException(0L));

        Assertions.assertThrows(NullParamException.class, () -> orderService.create(orderDTONull));
        Assertions.assertThrows(NullParamException.class, () -> orderService.create(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderService.create(orderDTONoValidId));
        Assertions.assertThrows(CreatedNotDefinedException.class, () -> orderService.create(orderDTOCreateNotDef));
        Assertions.assertThrows(NoValidPriceException.class, () -> orderService.create(orderDTONoValidPrice));
        Assertions.assertThrows(CompletedBeforeCreatedException.class, () -> orderService.create(orderDTOBeforeComplete));
        Assertions.assertThrows(OrderAlreadyExistException.class, () -> orderService.create(orderDTOExist));
    }

    //update
    @Test
    void updateTest() {
        Order specifiedOrder = Mockito.mock(Order.class);
        OrderNoRefDTO specifiedOrderDTO = Mockito.mock(OrderNoRefDTO.class);

        Mockito.when(specifiedOrderDTO.toOrder(baristaRepository, coffeeRepository))
                .thenReturn(specifiedOrder);
        Mockito.when(orderRepository.update(specifiedOrder))
                .thenReturn(specifiedOrder);

        Order resultOrder = orderService.update(specifiedOrderDTO);

        assertEquals(specifiedOrder, resultOrder);
    }

    @Test
    void updateWrongTest() {
        Order specifiedOrder = Mockito.mock(Order.class);
        OrderNoRefDTO orderDTONull = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTONoValidId = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTOCreateNotDef = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTOBeforeComplete = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTONoValidPrice = Mockito.mock(OrderNoRefDTO.class);
        OrderNoRefDTO orderDTONotFound = Mockito.mock(OrderNoRefDTO.class);


        Mockito.when(orderDTONull.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(NullParamException.class);
        Mockito.when(orderDTONoValidId.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(NoValidIdException.class);
        Mockito.when(orderDTOCreateNotDef.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(CreatedNotDefinedException.class);
        Mockito.when(orderDTOBeforeComplete.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(CompletedBeforeCreatedException.class);
        Mockito.when(orderDTONoValidPrice.toOrder(baristaRepository, coffeeRepository))
                .thenThrow(NoValidPriceException.class);
        Mockito.when(orderDTONotFound.toOrder(baristaRepository, coffeeRepository))
                .thenReturn(specifiedOrder);
        Mockito.when(orderRepository.update(specifiedOrder))
                .thenThrow(OrderNotFoundException.class);

        Assertions.assertThrows(NullParamException.class, () -> orderService.update(orderDTONull));
        Assertions.assertThrows(NullParamException.class, () -> orderService.update(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderService.update(orderDTONoValidId));
        Assertions.assertThrows(CreatedNotDefinedException.class, () -> orderService.update(orderDTOCreateNotDef));
        Assertions.assertThrows(NoValidPriceException.class, () -> orderService.update(orderDTONoValidPrice));
        Assertions.assertThrows(CompletedBeforeCreatedException.class, () -> orderService.update(orderDTOBeforeComplete));
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.update(orderDTONotFound));
    }

    //delete
    @Test
    void deleteTest() {
        Long specifiedId = 0L;

        Assertions.assertDoesNotThrow(() -> orderService.delete(specifiedId));
        Mockito.verify(orderRepository).delete(specifiedId);
    }

    @Test
    void deleteWrongTest() {
        //todo check id not found exception
        Assertions.assertThrows(NullParamException.class, () -> orderService.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderService.delete(-1L));
    }

    //complete
    @Test
    void completeTest() {
        Order specifiedOrder = new Order(0L, Mockito.mock(Barista.class), new ArrayList<>(), LocalDateTime.now().minusMinutes(1), null, 299.0);

        Mockito.when(orderRepository.findById(0L))
                .thenReturn(Optional.of(specifiedOrder));
        Mockito.when(orderRepository.update(specifiedOrder))
                .thenReturn(specifiedOrder);

        Order resultOrder = orderService.completeOrder(0L);

        assertNotNull(resultOrder.getCompleted());
        assertTrue(specifiedOrder.getCreated().isBefore(resultOrder.getCompleted()));
    }

    @Test
    void completeWrongTest() {
        Order specifiedOrder = new Order(0L, Mockito.mock(Barista.class), new ArrayList<>(), LocalDateTime.now().minusMinutes(1), null, 299.0);

        Mockito.when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        Mockito.when(orderRepository.findById(0L))
                .thenReturn(Optional.of(specifiedOrder));
        Mockito.when(orderRepository.update(specifiedOrder))
                .thenReturn(specifiedOrder);

        Assertions.assertDoesNotThrow(() -> orderService.completeOrder(0L));
        Assertions.assertThrows(OrderAlreadyCompletedException.class, () -> orderService.completeOrder(0L));

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.completeOrder(1L));

        Assertions.assertThrows(NullParamException.class, () -> orderService.completeOrder(null));
        Assertions.assertThrows(NoValidIdException.class, () -> orderService.completeOrder(-1L));
    }
}