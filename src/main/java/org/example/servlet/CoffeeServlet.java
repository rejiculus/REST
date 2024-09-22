package org.example.servlet;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Coffee;
import org.example.entity.exception.*;
import org.example.service.imp.CoffeeService;
import org.example.servlet.dto.CoffeeCreateDTO;
import org.example.servlet.dto.CoffeePublicDTO;
import org.example.servlet.dto.CoffeeUpdateDTO;
import org.example.servlet.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet(value = "/coffee/*")
public class CoffeeServlet extends SimpleServlet {
    private static final Logger log = Logger.getLogger(CoffeeServlet.class.getName());
    private final transient CoffeeService coffeeService;
    private final transient ConnectionManager connectionManager;
    private final transient Gson mapper;

    public static final String BAD_PATH = "Bad path! Path '%s' is not processing!";

    public CoffeeServlet() throws SQLException {
        connectionManager = new ConnectionManagerImp();
        mapper = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        coffeeService = new CoffeeService(connectionManager.getConnection());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            Map<String, String[]> params = req.getParameterMap();
            resp.setContentType("text/html");

            if (pathInfo == null || pathInfo.equals("/")) {
                if (params.containsKey("page") && params.containsKey("limit")) {
                    int page = Integer.parseInt(params.get("page")[0]);
                    int limit = Integer.parseInt(params.get("limit")[0]);

                    findAllByPage(page, limit, resp);
                } else {
                    findAll(resp);
                }
            } else if (pathInfo.matches("/\\d+/?")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);

                findById(id, resp);
            } else {
                String message = String.format("Bad request: %s", pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }


        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (CoffeeNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private void findAll(HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();

        List<CoffeePublicDTO> coffeeDtoList = coffeeService.findAll().stream()
                .map(CoffeePublicDTO::new)
                .toList();
        String json = arrToJson(coffeeDtoList, mapper);

        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    private void findById(Long id, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();

        Coffee coffee = coffeeService.findById(id);
        String json = mapper.toJson(new CoffeePublicDTO(coffee));

        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    private void findAllByPage(int page, int limit, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();

        List<CoffeePublicDTO> coffeeDtoList = coffeeService.findAllByPage(page, limit)
                .stream()
                .map(CoffeePublicDTO::new)
                .toList();
        String json = arrToJson(coffeeDtoList, mapper);

        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setCharacterEncoding("UTF-8");
            if (pathInfo == null || pathInfo.matches("/")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                create(req, resp);
                resp.sendError(HttpServletResponse.SC_CREATED);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }

        } catch (NoValidIdException | NoValidTipSizeException | NoValidNameException |
                 NullParamException e) {
            String message = String.format("Bad Request: %s", e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        CoffeeCreateDTO coffeeDTO = mapper.fromJson(request.getReader(), CoffeeCreateDTO.class);

        Coffee coffee = coffeeService.create(coffeeDTO);

        String json = mapper.toJson(new CoffeePublicDTO(coffee));
        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.flushBuffer();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setCharacterEncoding("UTF-8");

            if (pathInfo != null && pathInfo.matches("\\/\\d+\\/?")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);
                update(id, req);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (NoValidIdException | NoValidPriceException | NoValidNameException |
                 NullParamException e) {
            String message = String.format("Bad Request: %s", e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (CoffeeNotFoundException e) {
            String message = String.format("Not found: %s", e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        } catch (JsonMappingException e) {
            String message = String.format("Bad param: %s", e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        }
    }

    private void update(Long id, HttpServletRequest request) throws IOException {
        CoffeeUpdateDTO coffeeDTO = mapper.fromJson(request.getReader(), CoffeeUpdateDTO.class);
        CoffeeUpdateDTO coffeeNoRefDTO = new CoffeeUpdateDTO(id, coffeeDTO.name(), coffeeDTO.price(), coffeeDTO.orderIdList());
        coffeeService.update(coffeeNoRefDTO);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo != null && pathInfo.matches("\\/\\d+\\/?")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);
                delete(id);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (CoffeeNotFoundException e) {
            String message = String.format("Not found: %s", e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        }
    }

    private void delete(Long id) {
        coffeeService.delete(id);
    }


    @Override
    public void destroy() {
        super.destroy();
        ((ConnectionManagerImp) connectionManager).close();
    }
}
