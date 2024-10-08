package org.example.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.repository.BaristaRepositoryImp;
import org.example.repository.CoffeeRepositoryImp;
import org.example.repository.OrderRepositoryImp;
import org.example.service.IOrderService;
import org.example.service.gateway.BaristaRepository;
import org.example.service.gateway.CoffeeRepository;
import org.example.service.gateway.OrderRepository;
import org.example.service.implementation.OrderService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderServletTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withInitScript("DB_script.sql");

    private static OrderServlet orderServlet;
    private static OrderRepository orderRepository;
    private static BaristaRepository baristaRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        ConnectionManager connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        orderRepository = new OrderRepositoryImp(connectionManager);
        baristaRepository = new BaristaRepositoryImp(connectionManager);
        CoffeeRepository coffeeRepository = new CoffeeRepositoryImp(connectionManager);

        IOrderService orderService = new OrderService(baristaRepository, coffeeRepository, orderRepository);

        orderServlet = new OrderServlet(orderService);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @BeforeEach
    void setUp() throws ServletException {
        ServletConfig servletConfig = mock(ServletConfig.class);
        orderServlet.init(servletConfig);

        ServletContext servletContext = mock(ServletContext.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);
    }


    @Test
    void testDoGetFindAll() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getPathInfo())
                .thenReturn("/");
        when(response.getWriter())
                .thenReturn(writer);

        orderServlet.doGet(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoGetFindAllByPage() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getPathInfo())
                .thenReturn("/");
        when(response.getWriter())
                .thenReturn(writer);
        when(request.getParameterMap())
                .thenReturn(Map.of("page", new String[]{"0"}, "limit", new String[]{"3"}));

        orderServlet.doGet(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ':', value = {"0:0", "-1:1", "0:-1", "name:1", "1:name"})
    void testDoGetFindAllByPageBadRequest(String page, String limit) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getPathInfo())
                .thenReturn("/");
        when(response.getWriter())
                .thenReturn(writer);
        when(request.getParameterMap())
                .thenReturn(Map.of("page", new String[]{page}, "limit", new String[]{limit}));

        orderServlet.doGet(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoGetFindById() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        Barista barista = new Barista("John Doe");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        orderRepository.create(order);

        when(request.getPathInfo())
                .thenReturn("/1");
        when(response.getWriter())
                .thenReturn(writer);

        orderServlet.doGet(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }


    @ParameterizedTest
    @ValueSource(strings = {"/-1", "/naemwlrkw"})
    void testDoGetWrong(String path) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getPathInfo())
                .thenReturn(path);
        when(response.getWriter())
                .thenReturn(writer);

        orderServlet.doGet(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
    }

    @Test
    void testDoGetQueue() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getPathInfo())
                .thenReturn("/queue");
        when(response.getWriter())
                .thenReturn(writer);

        orderServlet.doGet(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoPostCreate() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        Barista barista = new Barista("John Doe");
        barista = baristaRepository.create(barista);
        String specifiedJson = "{\n" +
                "    \"baristaId\":" + barista.getId() + ",\n" +
                "    \"coffeeIdList\":[]\n" +
                "}";
        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));

        when(request.getReader())
                .thenReturn(bufferedReader);
        when(request.getPathInfo())
                .thenReturn("/");
        when(response.getWriter())
                .thenReturn(writer);

        orderServlet.doPost(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/1", "/1/1", "/name", "/1a"})
    void testDoPostBadRequest(String path) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo())
                .thenReturn(path);

        orderServlet.doPost(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoPutUpdate() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        Barista barista = new Barista("John Doe");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        order = orderRepository.create(order);

        String specifiedJson = String.format("""
                {
                    "baristaId":%d,
                    "coffeeIdList":[],
                    "created":"2024-09-13 14:20:00",
                    "price":0.0
                }""", barista.getId());

        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));


        when(request.getReader())
                .thenReturn(bufferedReader);
        when(request.getPathInfo())
                .thenReturn("/" + order.getId());
        when(response.getWriter())
                .thenReturn(writer);

        orderServlet.doPut(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "-1;[];\"2024-09-13 14:20:00\";0.0",
            "1;[0];\"2024-09-13 14:20:00\";0.0",
            "1;[-1];\"2024-09-13 14:20:00\";0.0",
            "1;[name];\"2024-09-13 14:20:00\";0.0",
            "1;[];;0.0",
            "1;[];naem;0.0",
            "1;[];\"2024-09-13 14:20:00\";-1.0",
            "1;[];\"2024-09-13 14:20:00\";NaN"
    })
    void testDoPutBadRequestCreate(String baristaId, String coffeeIdList, String created, String price) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Barista barista = new Barista("John Doe");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        orderRepository.create(order);

        String specifiedJson = String.format("""
                {
                    "baristaId":%s,
                    "coffeeIdList":%s,
                    "created":%s,
                    "price":%s
                }""", baristaId, coffeeIdList, created, price);

        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));

        when(request.getPathInfo())
                .thenReturn("/1");
        when(request.getReader())
                .thenReturn(bufferedReader);

        orderServlet.doPut(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "/-1", "/1/1", "/name", "/1a"})
    void testDoPutBadRequest(String path) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo())
                .thenReturn(path);

        orderServlet.doPut(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoPutNotFound() throws IOException {
        HttpServletRequest requestNotFound = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Barista barista = new Barista("John Doe");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        orderRepository.create(order);

        String specifiedJson = String.format("""
                {
                    "baristaId":%d,
                    "coffeeIdList":[],
                    "created":"2024-09-13 14:20:00",
                    "price":0.0
                }""", barista.getId());

        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));

        when(requestNotFound.getPathInfo())
                .thenReturn("/99");
        when(requestNotFound.getReader())
                .thenReturn(bufferedReader);

        orderServlet.doPut(requestNotFound, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }


    @Test
    void testDoDelete() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        Barista barista = new Barista("John Doe");
        barista = baristaRepository.create(barista);
        Order order = new Order(barista, List.of());
        order.setCreated(LocalDateTime.now());
        order = orderRepository.create(order);

        when(request.getPathInfo())
                .thenReturn("/" + order.getId());
        when(response.getWriter())
                .thenReturn(writer);

        orderServlet.doDelete(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "/-1", "/1/1", "/name", "/1a"})
    void testDoDeleteBadRequest(String path) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo())
                .thenReturn(path);

        orderServlet.doDelete(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoDeleteNotFound() throws IOException {
        HttpServletRequest requestNotFound = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(requestNotFound.getPathInfo())
                .thenReturn("/99/");

        orderServlet.doDelete(requestNotFound, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

}