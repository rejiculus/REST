package org.example.servlet;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.service.IOrderService;
import org.example.service.exception.OrderAlreadyCompletedException;
import org.example.service.exception.OrderHasReferencesException;
import org.example.service.imp.OrderService;
import org.example.servlet.dto.OrderCreateDTO;
import org.example.servlet.dto.OrderPublicDTO;
import org.example.servlet.dto.OrderUpdateDTO;
import org.example.servlet.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet(name = "OrderServlet", value = "/orders/*")
public class OrderServlet extends SimpleServlet {

    private static final Logger log = Logger.getLogger(OrderServlet.class.getName());
    private final transient IOrderService orderService;
    private final transient ConnectionManager connectionManager;
    private final transient Gson mapper;

    public static final String BAD_PATH = "Bad path! Path '%s' is not processing!";
    public static final String NOT_FOUND = "Not found: %s";
    public static final String HAS_REF = "Has references: %s";

    public OrderServlet() throws SQLException {
        connectionManager = new ConnectionManagerImp();
        orderService = new OrderService(connectionManager.getConnection());
        mapper = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            String pathInfo = req.getPathInfo();
            resp.setContentType("text/html");

            if (pathInfo == null || pathInfo.equals("/")) {
                Map<String, String[]> params = req.getParameterMap();
                if (params.containsKey("page") && params.containsKey("limit")) {
                    int page = Integer.parseInt(params.get("page")[0]);
                    int limit = Integer.parseInt(params.get("limit")[0]);

                    findAllByPage(page, limit, resp);
                } else {
                    findAll(resp);
                }
            } else if (pathInfo.matches("\\/queue\\/?")) { // regex match "/queue" or "/queue/"
                getQueue(resp);
            } else if (pathInfo.matches("\\/\\d+\\/?")) {//regex match "/[цифры]/" or "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);
                findById(id, resp);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (OrderNotFoundException e) {
            String message = String.format(NOT_FOUND, e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void findAll(HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        List<OrderPublicDTO> orderList = orderService.findAll().stream()
                .map(OrderPublicDTO::new)
                .toList();

        String json = arrToJson(orderList, mapper);
        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    private void findAllByPage(int page, int limit, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        List<OrderPublicDTO> orderList = orderService.findAllByPage(page, limit).stream()
                .map(OrderPublicDTO::new)
                .toList();

        String json = arrToJson(orderList, mapper);
        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    private void getQueue(HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        List<OrderPublicDTO> orderDtoList = orderService.getOrderQueue().stream()
                .map(OrderPublicDTO::new)
                .toList();
        String json = arrToJson(orderDtoList, mapper);
        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    private void findById(Long id, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        Order order = orderService.findById(id);
        String json = mapper.toJson(new OrderPublicDTO(order));
        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setCharacterEncoding("UTF-8");

            if (pathInfo == null || pathInfo.matches("/?")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                create(req, resp);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.info(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (NoValidIdException | NoValidPriceException | NoValidNameException |
                 NullParamException e) {
            log.severe("Param error: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (CoffeeNotFoundException | BaristaNotFoundException e) {
            String message = String.format(NOT_FOUND, e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        OrderCreateDTO orderDTO = mapper.fromJson(request.getReader(), OrderCreateDTO.class);

        Order order = orderService.create(orderDTO);

        String json = mapper.toJson(new OrderPublicDTO(order));
        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.flushBuffer();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setCharacterEncoding("UTF-8");

            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(BAD_PATH, pathInfo));
            } else if (pathInfo.matches("/\\d+/?")) {//regex match "/[цифры]/" or "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);

                update(id, req);

                resp.setStatus(HttpServletResponse.SC_OK);
            } else if (pathInfo.matches("\\/\\d+\\/complete\\/?")) { //regex match "/[digits]/complete" or "/[digits]/complete/"
                Long id = Long.parseLong(pathInfo.split("/")[1]);

                complete(id);

                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (NoValidIdException | NoValidPriceException | NoValidNameException |
                 NullParamException e) {
            String message = String.format(BAD_PATH, e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (OrderNotFoundException e) {
            String message = String.format(BAD_PATH, e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        } catch (OrderAlreadyCompletedException e) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, e.getMessage());
        } catch (JsonMappingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad param! " + e);
        }
    }

    private void update(Long id, HttpServletRequest request) throws IOException {
        OrderUpdateDTO orderDTO = mapper.fromJson(request.getReader(), OrderUpdateDTO.class);
        OrderUpdateDTO orderNoRefDTO = new OrderUpdateDTO(id,
                orderDTO.baristaId(),
                orderDTO.created(),
                orderDTO.completed(),
                orderDTO.price(),
                orderDTO.coffeeIdList());

        orderService.update(orderNoRefDTO);
    }

    private void complete(Long id) {
        orderService.completeOrder(id);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo != null && pathInfo.matches("/\\d+/?")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);

                delete(id);

                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (OrderNotFoundException e) {
            String message = String.format(NOT_FOUND, e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        }catch (OrderHasReferencesException e) {
            String message = String.format(HAS_REF, e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        }
    }

    private void delete(Long id) {
        orderService.delete(id);
    }


    @Override
    public void destroy() {
        super.destroy();
        ((ConnectionManagerImp) connectionManager).close();
    }
}
