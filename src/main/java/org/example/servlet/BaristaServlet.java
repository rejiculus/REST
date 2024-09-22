package org.example.servlet;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Barista;
import org.example.entity.exception.*;
import org.example.service.imp.BaristaService;
import org.example.servlet.dto.BaristaCreateDTO;
import org.example.servlet.dto.BaristaPublicDTO;
import org.example.servlet.dto.BaristaUpdateDTO;
import org.example.servlet.adapter.LocalDateTimeAdapter;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet(value = "/barista/*")
public class BaristaServlet extends SimpleServlet {
    private static final Logger log = Logger.getLogger(BaristaServlet.class.getName());
    private final transient BaristaService baristaService;
    private final transient ConnectionManager connectionManager;
    private final transient Gson mapper;

    public static final String BAD_PATH = "Bad path! Path '%s' is not processing!";

    public BaristaServlet() throws SQLException {
        connectionManager = new ConnectionManagerImp();
        mapper = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        this.baristaService = new BaristaService(connectionManager.getConnection());
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

            } else if (pathInfo.matches("/\\d+/?")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);
                findById(id, resp);
            } else {
                String message = String.format("Bad request: %s", pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }


        } catch (NumberFormatException e) {
            log.severe(e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (BaristaNotFoundException e) {
            log.severe(e.getMessage());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private void findAll(HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        List<BaristaPublicDTO> baristaDtoList = baristaService.findAll().stream()
                .map(BaristaPublicDTO::new)
                .toList();

        String json = arrToJson(baristaDtoList, mapper);

        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    private void findAllByPage(int page, int limit, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        List<BaristaPublicDTO> baristaDtoList = baristaService.findAllByPage(page, limit)
                .stream()
                .map(BaristaPublicDTO::new)
                .toList();

        String json = arrToJson(baristaDtoList, mapper);

        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    private void findById(Long id, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        Barista barista = baristaService.findById(id);
        String json = mapper.toJson(new BaristaPublicDTO(barista));

        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setCharacterEncoding("UTF-8");

            if (pathInfo == null || pathInfo.matches("/?")) {//regex путь соответствующий "/" или ""
                create(req, resp);
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (NoValidIdException | NoValidTipSizeException | NoValidNameException |
                 NullParamException e) {
            log.severe(e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        BaristaCreateDTO baristaDTO = mapper.fromJson(request.getReader(), BaristaCreateDTO.class);
        Barista barista = baristaService.create(baristaDTO);

        String json = mapper.toJson(new BaristaPublicDTO(barista));
        printWriter.write(json);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.flushBuffer();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.matches("\\/\\d+\\/?")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);
                update(id, req);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.flushBuffer();
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }

        } catch (NoValidIdException | NoValidTipSizeException | NoValidNameException |
                 NullParamException e) {
            log.severe(e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (BaristaNotFoundException e) {
            String message = String.format("Bad order: %s", e.getMessage());
            log.severe(message);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        } catch (NumberFormatException e) {
            log.severe(e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad param!");
        } catch (JsonMappingException e) {
            log.severe(e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad param! " + e);
        }
    }

    private void update(Long id, HttpServletRequest request) throws IOException {
        BaristaUpdateDTO barista = mapper.fromJson(request.getReader(), BaristaUpdateDTO.class);
        BaristaUpdateDTO baristaDTO = new BaristaUpdateDTO(id, barista.fullName(), barista.tipSize(), barista.orderIdList());
        baristaService.update(baristaDTO);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.matches("\\/\\d+\\/?")) {//regex путь соответствующий "/[цифры]/" или "/[цифры]"
                Long id = Long.parseLong(pathInfo.split("/")[1]);
                delete(id);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.flushBuffer();
            } else {
                String message = String.format(BAD_PATH, pathInfo);
                log.severe(message);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            }
        } catch (BaristaNotFoundException e) {
            log.severe(e.getMessage());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private void delete(Long id) {
        baristaService.delete(id);
    }

    @Override
    public void destroy() {
        super.destroy();
        ((ConnectionManagerImp) connectionManager).close();
    }
}
