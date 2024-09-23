package org.example.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Coffee;
import org.example.repository.CoffeeRepository;
import org.example.repository.imp.CoffeeRepositoryImp;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CoffeeServletTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine3.20")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            .withCopyFileToContainer(MountableFile.forClasspathResource("DB_script.sql"),
                    "/docker-entrypoint-initdb.d/01-schema.sql");

    private static CoffeeServlet coffeeServlet;
    private static CoffeeRepository coffeeRepository;

    @BeforeAll
    static void beforeAll() throws SQLException {
        postgres.start();

        ConnectionManager connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Connection connection = connectionManager.getConnection();

        coffeeServlet = new CoffeeServlet(connectionManager);
        coffeeRepository = new CoffeeRepositoryImp(connection);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @BeforeEach
    void setUp() throws ServletException {
        ServletConfig servletConfig = mock(ServletConfig.class);
        coffeeServlet.init(servletConfig);

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

        coffeeServlet.doGet(request, response);

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

        coffeeServlet.doGet(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoGetFindById() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        coffeeRepository.create(new Coffee("John Doe", 100.0));

        when(request.getPathInfo())
                .thenReturn("/1");
        when(response.getWriter())
                .thenReturn(writer);

        coffeeServlet.doGet(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }


    @Test
    void testDoGetWrong() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        coffeeRepository.create(new Coffee("John Doe", 100.0));
        when(request.getPathInfo())
                .thenReturn("/1qsdsfd/");
        when(response.getWriter())
                .thenReturn(writer);

        coffeeServlet.doGet(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
    }

    @Test
    void testDoGetNotFound() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);


        when(request.getPathInfo())
                .thenReturn("/99/");
        when(response.getWriter())
                .thenReturn(writer);

        coffeeServlet.doGet(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

    @Test
    void testDoGetWrongId() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);


        when(request.getPathInfo())
                .thenReturn("/-1/");
        when(response.getWriter())
                .thenReturn(writer);

        coffeeServlet.doGet(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }


    @Test
    void testDoPostCreate() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        String specifiedJson = """
                {
                    "name":"Alabasta",
                    "price":300.0
                }
                """;
        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));

        when(request.getReader())
                .thenReturn(bufferedReader);
        when(request.getPathInfo())
                .thenReturn("/");
        when(response.getWriter())
                .thenReturn(writer);

        coffeeServlet.doPost(request, response);

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

        coffeeServlet.doPost(request1, response);
        coffeeServlet.doPost(request2, response);

        Mockito.verify(response, times(2)).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoPutUpdate() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        String specifiedJson = """
                {
                    "name":"Alabasta",
                    "price":300.0,
                    "orderIdList":[]
                }
                """;
        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));
        Coffee coffee = coffeeRepository.create(new Coffee("John Doe", 100.0));

        when(request.getReader())
                .thenReturn(bufferedReader);
        when(request.getPathInfo())
                .thenReturn("/" + coffee.getId());
        when(response.getWriter())
                .thenReturn(writer);

        coffeeServlet.doPut(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoPutBadRequestCreate() throws IOException {
        HttpServletRequest request1 = mock(HttpServletRequest.class);
        HttpServletRequest request0 = mock(HttpServletRequest.class);
        HttpServletRequest request2 = mock(HttpServletRequest.class);
        HttpServletRequest requestNotFound = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String specifiedJson = """
                {
                "name":"Alabasta",
                "price":300.0,
                "orderIdList":[]
                }
                """;
        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));

        when(request1.getPathInfo())
                .thenReturn("/");
        when(request2.getPathInfo())
                .thenReturn("/1/1");
        when(requestNotFound.getPathInfo())
                .thenReturn("/99");
        when(requestNotFound.getReader())
                .thenReturn(bufferedReader);

        coffeeServlet.doPut(request0, response);
        coffeeServlet.doPut(request1, response);
        coffeeServlet.doPut(request2, response);
        coffeeServlet.doPut(requestNotFound, response);

        Mockito.verify(response, times(3)).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
        Mockito.verify(response, times(1)).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }


    @Test
    void testDoDelete() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        coffeeRepository.create(new Coffee("John Doe", 100.0));

        when(request.getPathInfo())
                .thenReturn("/1");
        when(response.getWriter())
                .thenReturn(writer);

        coffeeServlet.doDelete(request, response);

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

        coffeeServlet.doDelete(request0, response);
        coffeeServlet.doDelete(request1, response);
        coffeeServlet.doDelete(request2, response);
        coffeeServlet.doDelete(requestNotFound, response);

        Mockito.verify(response, times(3)).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
        Mockito.verify(response, times(1)).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

}