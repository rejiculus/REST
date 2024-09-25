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
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.repository.imp.BaristaRepositoryImp;
import org.example.repository.imp.CoffeeRepositoryImp;
import org.example.repository.imp.OrderRepositoryImp;
import org.example.service.IOrderService;
import org.example.service.imp.OrderService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServletTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine3.20")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            .withCopyFileToContainer(MountableFile.forClasspathResource("DB_script.sql"),
                    "/docker-entrypoint-initdb.d/01-schema.sql");

    private static OrderServlet orderServlet;
    private static OrderRepository orderRepository;
    private static BaristaRepository baristaRepository;

    @BeforeAll
    static void beforeAll() throws SQLException {
        postgres.start();

        ConnectionManager connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Connection connection = connectionManager.getConnection();

        orderRepository = new OrderRepositoryImp(connection);
        baristaRepository = new BaristaRepositoryImp(connection);
        CoffeeRepository coffeeRepository = new CoffeeRepositoryImp(connection);

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


    @Test
    void testDoGetWrong() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getPathInfo())
                .thenReturn("/1qsdsfd/");
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

    @Test
    void testDoPostBadRequestCreate() throws IOException {
        HttpServletRequest request1 = mock(HttpServletRequest.class);
        HttpServletRequest request2 = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request1.getPathInfo())
                .thenReturn("/1");
        when(request2.getPathInfo())
                .thenReturn("/1/1");

        orderServlet.doPost(request1, response);
        orderServlet.doPost(request2, response);

        Mockito.verify(response, times(2)).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
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

    @Test
    void testDoPutBadRequestCreate() throws IOException {
        HttpServletRequest request1 = mock(HttpServletRequest.class);
        HttpServletRequest request0 = mock(HttpServletRequest.class);
        HttpServletRequest request2 = mock(HttpServletRequest.class);
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

        when(request1.getPathInfo())
                .thenReturn("/");
        when(request2.getPathInfo())
                .thenReturn("/1/1");
        when(requestNotFound.getPathInfo())
                .thenReturn("/99");
        when(requestNotFound.getReader())
                .thenReturn(bufferedReader);

        orderServlet.doPut(request0, response);
        orderServlet.doPut(request1, response);
        orderServlet.doPut(request2, response);
        orderServlet.doPut(requestNotFound, response);

        Mockito.verify(response, times(3)).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
        Mockito.verify(response, times(1)).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
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

    @Test
    void testDoDeleteBadRequestCreate() throws IOException {
        HttpServletRequest request1 = mock(HttpServletRequest.class);
        HttpServletRequest request0 = mock(HttpServletRequest.class);
        HttpServletRequest request2 = mock(HttpServletRequest.class);
        HttpServletRequest requestNotFound = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request1.getPathInfo())
                .thenReturn("/");
        when(request2.getPathInfo())
                .thenReturn("/1/1");
        when(requestNotFound.getPathInfo())
                .thenReturn("/99/");

        orderServlet.doDelete(request0, response);
        orderServlet.doDelete(request1, response);
        orderServlet.doDelete(request2, response);
        orderServlet.doDelete(requestNotFound, response);

        Mockito.verify(response, times(3)).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
        Mockito.verify(response, times(1)).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

}