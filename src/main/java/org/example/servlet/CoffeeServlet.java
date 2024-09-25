package org.example.servlet;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.Coffee;
import org.example.entity.exception.*;
import org.example.repository.exception.DataBaseException;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.service.ICoffeeService;
import org.example.service.exception.CoffeeHasReferenceException;
import org.example.servlet.adapter.LocalDateTimeAdapter;
import org.example.servlet.dto.CoffeeCreateDTO;
import org.example.servlet.dto.CoffeePublicDTO;
import org.example.servlet.dto.CoffeeUpdateDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CoffeeServlet extends SimpleServlet {
    private static final Logger LOGGER = Logger.getLogger(CoffeeServlet.class.getName());
    private final transient ICoffeeService coffeeService;
    private final transient Gson mapper = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private static final String BAD_PATH = "Bad path! Path '%s' is not processing!";
    private static final String NOT_FOUND = "Not found: %s";
    private static final String BAD_PARAMS = "Bad params: %s";
    public static final String HAS_REF = "Has references: %s";
    private static final String SOME_DATA_BASE_EXCEPTION = "Some database error: %s";

    private static final String SPECIFIED_COFFEE_REGEX = "/\\d+/?"; //regex путь соответствующий "/[цифры]/" или "/[цифры]"


    public CoffeeServlet(ICoffeeService coffeeService) {
        if (coffeeService == null)
            throw new NullParamException();
        this.coffeeService = coffeeService;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            Map<String, String[]> params = req.getParameterMap();

            if (pathInfo == null || pathInfo.equals("/")) {
                if (params.containsKey("page") && params.containsKey("limit")) {
                    int page = Integer.parseInt(params.get("page")[0]);
                    int limit = Integer.parseInt(params.get("limit")[0]);

                    findAllByPage(page, limit, resp);
                } else {
                    findAll(resp);
                }
            } else if (pathInfo.matches(SPECIFIED_COFFEE_REGEX)) { //regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);

                findById(id, resp);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                LOGGER.info(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }


        } catch (NoValidPageException | NoValidLimitException | NullParamException | NoValidIdException |
                 NumberFormatException | JsonMappingException | JsonSyntaxException e) {
            String message = String.format(BAD_PARAMS, e.getMessage());
            LOGGER.info(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (CoffeeNotFoundException e) {
            String message = String.format(NOT_FOUND, e.getMessage());
            LOGGER.info(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        } catch (DataBaseException e) {
            String message = String.format(SOME_DATA_BASE_EXCEPTION, e.getMessage());
            LOGGER.severe(message);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
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

            if (pathInfo == null || pathInfo.matches("/")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                create(req, resp);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                LOGGER.info(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }

        } catch (NoValidIdException | NoValidPriceException | JsonSyntaxException | NoValidNameException |
                 NullParamException | JsonMappingException e) {
            String message = String.format(BAD_PARAMS, e.getMessage());
            LOGGER.info(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (DataBaseException e) {
            String message = String.format(SOME_DATA_BASE_EXCEPTION, e.getMessage());
            LOGGER.severe(message);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
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

            if (pathInfo != null && pathInfo.matches(SPECIFIED_COFFEE_REGEX)) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);
                update(id, req);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                LOGGER.info(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (NoValidIdException | NoValidPriceException | NoValidNameException |
                 NullParamException | JsonMappingException | NumberFormatException |
                 JsonSyntaxException e) {
            String message = String.format(BAD_PARAMS, e.getMessage());
            LOGGER.info(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (CoffeeNotFoundException e) {
            String message = String.format(NOT_FOUND, e.getMessage());
            LOGGER.info(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        } catch (DataBaseException e) {
            String message = String.format(SOME_DATA_BASE_EXCEPTION, e.getMessage());
            LOGGER.severe(message);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
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

            if (pathInfo != null && pathInfo.matches(SPECIFIED_COFFEE_REGEX)) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);
                delete(id);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                LOGGER.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (NumberFormatException | NullParamException | NoValidIdException e) {
            String message = String.format(BAD_PARAMS, e.getMessage());
            LOGGER.info(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (CoffeeNotFoundException e) {
            String message = String.format(NOT_FOUND, e.getMessage());
            LOGGER.severe(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        } catch (CoffeeHasReferenceException e) {
            String message = String.format(HAS_REF, e.getMessage());
            LOGGER.severe(message);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (DataBaseException e) {
            String message = String.format(SOME_DATA_BASE_EXCEPTION, e.getMessage());
            LOGGER.severe(message);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private void delete(Long id) {
        coffeeService.delete(id);
    }

}
