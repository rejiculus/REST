package org.example.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.repository.imp.TESTBaristaRepository;
import org.example.repository.imp.TestCoffeeRepository;
import org.example.repository.imp.TestOrderRepository;
import org.example.service.imp.OrderService;
import org.example.servlet.dto.OrderNoRefDTO;
import org.example.servlet.dto.OrderPublicDTO;
import org.example.servlet.mapper.OrderDtoMapper;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderServlet", value = "/orders/*")
public class OrderServlet extends SimpleServlet {
    private final transient OrderService orderService = new OrderService(new TestOrderRepository(), new TESTBaristaRepository(), new TestCoffeeRepository());
    private final transient OrderDtoMapper mapper = new OrderDtoMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("text/html");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<OrderPublicDTO> orderList = orderService.findAll().stream()
                    .map(OrderPublicDTO::new)
                    .toList();

            String json = arrToJson(orderList, mapper);
            jsonWriter(resp, json);
            return;
        }

        String[] splits = pathInfo.split("/");

        if (splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Long id = Long.parseLong(splits[1]);
            Order order = orderService.findById(id);
            String json = mapper.map(new OrderPublicDTO(order));
            jsonWriter(resp, json);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");

        if (!(pathInfo == null || pathInfo.equals("/"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("Bad path! Path '%s' is not processing!", pathInfo));
            return;
        }


        try {
            String requestBody = getRequestBody(req);
            OrderNoRefDTO orderDTO = mapper.map(requestBody);

            Order order = orderService.create(orderDTO);

            String json = mapper.map(new OrderPublicDTO(order));
            jsonWriter(resp, json);

        } catch (LessZeroException | NoValidIdException | InfiniteException | NaNException | NoValidNameException |
                 NullParamException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Throwable e) {
            log(e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");

        if (!(pathInfo == null || pathInfo.equals("/"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("Bad path! Path '%s' is not processing!", pathInfo));
            return;
        }


        try {
            String requestBody = getRequestBody(req);
            OrderNoRefDTO orderDTO = mapper.map(requestBody);

            orderService.update(orderDTO);

        } catch (LessZeroException | NoValidIdException | InfiniteException | NaNException | NoValidNameException |
                 NullParamException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Throwable e) {
            log(e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] splits = pathInfo.split("/");

        try {
            Long id = Long.parseLong(splits[1]);
            orderService.delete(id);

        } catch (RuntimeException e) {//todo
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
