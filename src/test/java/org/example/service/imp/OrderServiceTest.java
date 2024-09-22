package org.example.service.imp;

import org.example.entity.Barista;
import org.example.entity.Coffee;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.service.exception.OrderAlreadyCompletedException;
import org.example.service.mapper.OrderDtoToOrderMapper;
import org.example.servlet.dto.OrderCreateDTO;
import org.example.servlet.dto.OrderUpdateDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

class OrderServiceTest {
    static AutoCloseable mocks;

    @Mock
    OrderRepository orderRepository;
    @Mock
    BaristaRepository baristaRepository;
    @Mock
    CoffeeRepository coffeeRepository;

    @Mock
    OrderDtoToOrderMapper mapper;

    OrderService orderService;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        orderService = new OrderService(
                baristaRepository,
                coffeeRepository,
                orderRepository
        );
    }

    @AfterAll
    public static void close() throws Exception {
        mocks.close();
    }

    @Test
    void constructorsTest() {
        Connection connection = Mockito.mock(Connection.class);
        Assertions.assertDoesNotThrow(() -> new OrderService(baristaRepository, coffeeRepository, orderRepository));
        Assertions.assertDoesNotThrow(() -> new OrderService(connection));
        Assertions.assertThrows(NullParamException.class, () -> new OrderService(null));
        Assertions.assertThrows(NullParamException.class, () -> new OrderService(null, coffeeRepository, orderRepository));
        Assertions.assertThrows(NullParamException.class, () -> new OrderService(baristaRepository, null, orderRepository));
        Assertions.assertThrows(NullParamException.class, () -> new OrderService(baristaRepository, coffeeRepository, null));
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
            order.setId((long) i);
            order.setCreated(LocalDateTime.now());
            if (i % 2 == 0)
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
        Barista specifiedBarista = Mockito.spy(new Barista("NAME"));
        Order specifiedOrder = Mockito.spy(new Order(specifiedBarista, new ArrayList<>()));
        OrderCreateDTO specifiedOrderDTO = Mockito.mock(OrderCreateDTO.class);

        Order expectedOrder = new Order(Mockito.mock(Barista.class), new ArrayList<>());
        expectedOrder.setId(99L);

        Long specificBaristaId = 0L;
        Long specificCoffeeId = 0L;
        Coffee specificCoffee = Mockito.spy(new Coffee(0L, "name", 0.0, List.of()));
        Barista specificBarista = Mockito.spy(new Barista(0L, "name", List.of(), 0.1));
        List<Long> specificCoffeeIdList = List.of(specificCoffeeId);

        Mockito.when(specifiedOrderDTO.baristaId())
                .thenReturn(specificBaristaId);
        Mockito.when(baristaRepository.findById(specificBaristaId))
                .thenReturn(Optional.of(specificBarista));

        Mockito.when(specifiedOrderDTO.coffeeIdList())
                .thenReturn(specificCoffeeIdList);
        Mockito.when(coffeeRepository.findById(specificCoffeeId))
                .thenReturn(Optional.of(specificCoffee));

        Mockito.when(orderRepository.create(any()))
                .thenReturn(expectedOrder);

        Order resultOrder = orderService.create(specifiedOrderDTO);

        assertEquals(expectedOrder, resultOrder);
    }

    @Test
    void createWrongTest() {


        OrderCreateDTO orderDTONull = Mockito.mock(OrderCreateDTO.class);
        Mockito.when(orderDTONull.baristaId())
                .thenReturn(null);
        Mockito.when(orderDTONull.coffeeIdList())
                .thenReturn(null);
        Mockito.when(baristaRepository.findById(null))
                .thenThrow(NullParamException.class);


        Assertions.assertThrows(NullParamException.class, () -> orderService.create(null));
        Assertions.assertThrows(NullParamException.class, () -> orderService.create(orderDTONull));
    }

    //update
    @Test
    void updateTest() {

        Coffee specificCoffee = Mockito.spy(new Coffee(0L, "name", 0.0, List.of()));
        Barista specificBarista = Mockito.spy(new Barista(0L, "name", List.of(), 0.1));
        Order specifiedOrder = Mockito.mock(Order.class);
        OrderUpdateDTO specifiedOrderDTO = Mockito.spy(new OrderUpdateDTO(0L, 0L, LocalDateTime.MIN, LocalDateTime.MAX, 0.0, List.of(0L)));

        Mockito.when(orderRepository.update(any()))
                .thenReturn(specifiedOrder);
        Mockito.when(specifiedOrder.getId())
                .thenReturn(0L);
        Mockito.when(baristaRepository.findById(0L))
                .thenReturn(Optional.of(specificBarista));
        Mockito.when(coffeeRepository.findById(0L))
                .thenReturn(Optional.of(specificCoffee));

        Order resultOrder = orderService.update(specifiedOrderDTO);

        assertEquals(specifiedOrder, resultOrder);
    }

    @Test
    void updateWrongTest() {
        Order specifiedOrder = Mockito.mock(Order.class);
        Barista specificBarista = Mockito.mock(Barista.class);
        Coffee specificCoffee = Mockito.mock(Coffee.class);
        OrderUpdateDTO orderDTONull = Mockito.spy(new OrderUpdateDTO(null, 0L, LocalDateTime.MIN, LocalDateTime.MAX, 0.0, List.of(0L)));
        OrderUpdateDTO orderDTONoValidId = Mockito.spy(new OrderUpdateDTO(-1L, 0L, LocalDateTime.MIN, LocalDateTime.MAX, 0.0, List.of(0L)));
        OrderUpdateDTO orderDTOCreateNotDef = Mockito.spy(new OrderUpdateDTO(0L, 0L, null, LocalDateTime.MAX, 0.0, List.of(0L)));
        OrderUpdateDTO orderDTOBeforeComplete = Mockito.spy(new OrderUpdateDTO(0L, 0L, LocalDateTime.MAX, LocalDateTime.MIN, 0.0, List.of(0L)));
        OrderUpdateDTO orderDTONoValidPrice = Mockito.spy(new OrderUpdateDTO(0L, 0L, LocalDateTime.MIN, LocalDateTime.MAX, -1.0, List.of(0L)));
        OrderUpdateDTO orderDTONotFound = Mockito.spy(new OrderUpdateDTO(99L, 0L, LocalDateTime.MIN, LocalDateTime.MAX, 1.0, List.of(0L)));

        Mockito.when(baristaRepository.findById(any()))
                .thenReturn(Optional.of(specificBarista));
        Mockito.when(coffeeRepository.findById(any()))
                .thenReturn(Optional.of(specificCoffee));
        Mockito.when(mapper.map(orderDTONoValidId))
                .thenThrow(NoValidIdException.class);
        Mockito.when(mapper.map(orderDTOCreateNotDef))
                .thenThrow(CreatedNotDefinedException.class);
        Mockito.when(mapper.map(orderDTOBeforeComplete))
                .thenThrow(CompletedBeforeCreatedException.class);
        Mockito.when(mapper.map(orderDTONoValidPrice))
                .thenThrow(NoValidPriceException.class);

        Mockito.when(specifiedOrder.getBarista())
                .thenReturn(specificBarista);
        Mockito.when(specificCoffee.getPrice())
                .thenReturn(102.0);
        Mockito.when(orderRepository.update(argThat(order -> order.getId().equals(99L))))
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


    @Test
    void findAllByPageTest() {
        List<Order> specifiedOrderListPage0 = new ArrayList<>(List.of(
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class)
        ));
        List<Order> specifiedOrderListPage1 = new ArrayList<>(List.of(
                Mockito.mock(Order.class),
                Mockito.mock(Order.class),
                Mockito.mock(Order.class)
        ));

        Mockito.when(orderRepository.findAllByPage(0, 4))
                .thenReturn(specifiedOrderListPage0);

        Mockito.when(orderRepository.findAllByPage(1, 4))
                .thenReturn(specifiedOrderListPage1);

        List<Order> resultOrderList = orderService.findAllByPage(0, 4);
        assertEquals(specifiedOrderListPage0, resultOrderList);

        resultOrderList = orderService.findAllByPage(1, 4);
        assertEquals(specifiedOrderListPage1, resultOrderList);
    }

    @Test
    void findAllByPageWrongTest() {
        Assertions.assertThrows(NoValidPageException.class, () -> orderService.findAllByPage(-1, 1));
        Assertions.assertThrows(NoValidLimitException.class, () -> orderService.findAllByPage(0, 0));
        Assertions.assertThrows(NoValidLimitException.class, () -> orderService.findAllByPage(0, -1));
    }
}