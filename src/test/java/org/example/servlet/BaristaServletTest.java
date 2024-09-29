package org.example.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Barista;
import org.example.repository.BaristaRepository;
import org.example.repository.OrderRepository;
import org.example.repository.imp.BaristaRepositoryImp;
import org.example.repository.imp.OrderRepositoryImp;
import org.example.service.IBaristaService;
import org.example.service.imp.BaristaService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BaristaServletTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withInitScript("DB_script.sql");

    private static BaristaServlet baristaServlet;
    private static BaristaRepository baristaRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        ConnectionManager connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        baristaRepository = new BaristaRepositoryImp(connectionManager);
        OrderRepository orderRepository = new OrderRepositoryImp(connectionManager);
        IBaristaService baristaService = new BaristaService(baristaRepository, orderRepository);

        baristaServlet = new BaristaServlet(baristaService);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @BeforeEach
    void setUp() throws ServletException {
        ServletConfig servletConfig = mock(ServletConfig.class);
        baristaServlet.init(servletConfig);

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

        baristaServlet.doGet(request, response);

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

        baristaServlet.doGet(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ':', value = {"0:0", "-1:1", "0:-1", "name:1", "1:name"})
    void testDoGetFindAllByPageWrong(String page, String limit) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getPathInfo())
                .thenReturn("/");
        when(response.getWriter())
                .thenReturn(writer);
        when(request.getParameterMap())
                .thenReturn(Map.of("page", new String[]{page}, "limit", new String[]{limit}));

        baristaServlet.doGet(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoGetFindById() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        Barista barista = baristaRepository.create(new Barista("John Doe"));

        when(request.getPathInfo())
                .thenReturn("/" + barista.getId());
        when(response.getWriter())
                .thenReturn(writer);

        baristaServlet.doGet(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "naemwlrkw"})
    void testDoGetWrong(String id) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        baristaRepository.create(new Barista("John Doe"));

        when(request.getPathInfo())
                .thenReturn("/" + id);
        when(response.getWriter())
                .thenReturn(writer);

        baristaServlet.doGet(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
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

        baristaServlet.doGet(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

    @Test
    void testDoPostCreate() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        String specifiedJson = """
                {
                    "fullName":"Name",
                    "tipSize":0.5
                }""";
        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));

        when(request.getReader())
                .thenReturn(bufferedReader);
        when(request.getPathInfo())
                .thenReturn("/");
        when(response.getWriter())
                .thenReturn(writer);

        baristaServlet.doPost(request, response);

        Mockito.verify(writer).write(anyString());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/1", "/1/1", "/name", "/1a"})
    void testDoPostBadRequestCreate(String path) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo())
                .thenReturn(path);

        baristaServlet.doPost(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoPutUpdate() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        String specifiedJson = """
                {
                    "fullName": "bbb",
                    "tipSize": 0.2,
                    "orderIdList": []
                }""";
        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));
        Barista barista = baristaRepository.create(new Barista("John Doe"));

        when(request.getReader())
                .thenReturn(bufferedReader);
        when(request.getPathInfo())
                .thenReturn("/" + barista.getId());
        when(response.getWriter())
                .thenReturn(writer);

        baristaServlet.doPut(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ':', value = {
            "bbb:-0.2:[]",
            "\"\":0.2:[]",
            "SOmeName:0.2:[name]",
            "SOmeName:NaN:[]",
            "SOmeName:0.2:[0]"

    })
    void testDoPutUpdateBadReq(String fullName, String tipSize, String orderIdList) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        String specifiedJson = String.format("""
                {
                    "fullName": %s,
                    "tipSize": %s,
                    "orderIdList": %s
                }""", fullName, tipSize, orderIdList);
        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new StringReader(specifiedJson)));
        Barista barista = baristaRepository.create(new Barista("John Doe"));

        when(request.getReader())
                .thenReturn(bufferedReader);
        when(request.getPathInfo())
                .thenReturn("/" + barista.getId());
        when(response.getWriter())
                .thenReturn(writer);

        baristaServlet.doPut(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }


    @ParameterizedTest
    @ValueSource(strings = {"", "/1/1", "/name", "/1a"})
    void testDoPutBadRequestUpdate(String path) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo())
                .thenReturn(path);

        baristaServlet.doPut(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoDelete() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Barista barista = baristaRepository.create(new Barista("John Doe"));

        when(request.getPathInfo())
                .thenReturn("/" + barista.getId());

        baristaServlet.doDelete(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/1/1", "/-1", "/name", ""})
    void testDoDeleteBadRequest(String path) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo())
                .thenReturn(path);

        baristaServlet.doDelete(request, response);

        Mockito.verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    void testDoDeleteNotFound() throws IOException {
        HttpServletRequest requestNotFound = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(requestNotFound.getPathInfo())
                .thenReturn("/99/");

        baristaServlet.doDelete(requestNotFound, response);

        Mockito.verify(response, times(1)).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }


}