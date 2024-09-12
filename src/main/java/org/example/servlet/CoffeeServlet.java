package org.example.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.Coffee;
import org.example.entity.exception.*;
import org.example.repository.imp.TestCoffeeRepository;
import org.example.repository.imp.TestOrderRepository;
import org.example.service.imp.CoffeeService;
import org.example.servlet.dto.CoffeeNoRefDTO;
import org.example.servlet.dto.CoffeePublicDTO;
import org.example.servlet.mapper.CoffeeDTOMapper;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "BaristaServlet", value = "/coffee/*")
public class CoffeeServlet extends SimpleServlet {
    private final transient CoffeeService coffeeService = new CoffeeService(new TestCoffeeRepository(), new TestOrderRepository());
    private final transient CoffeeDTOMapper mapper = new CoffeeDTOMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("text/html");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<CoffeePublicDTO> coffeeDtoList = coffeeService.findAll().stream()
                    .map(CoffeePublicDTO::new)
                    .toList();

            String json = arrToJson(coffeeDtoList, mapper);

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
            Coffee coffee = coffeeService.findById(id);
            String json = mapper.map(new CoffeePublicDTO(coffee));
            jsonWriter(resp, json);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Throwable e) {
            log(e.getMessage());
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
            CoffeeNoRefDTO coffeeDTO = mapper.map(requestBody);

            Coffee coffee = coffeeService.create(coffeeDTO);

            String json = mapper.map(new CoffeePublicDTO(coffee));
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
            CoffeeNoRefDTO coffeeDTO = mapper.map(requestBody);

            coffeeService.update(coffeeDTO);

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
            coffeeService.delete(id);

        } catch (RuntimeException e) {//todo
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
