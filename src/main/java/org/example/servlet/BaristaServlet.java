package org.example.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.Barista;
import org.example.entity.exception.*;
import org.example.repository.imp.TESTBaristaRepository;
import org.example.repository.imp.TestOrderRepository;
import org.example.service.imp.BaristaService;
import org.example.servlet.dto.BaristaNoRefDTO;
import org.example.servlet.dto.BaristaPublicDTO;
import org.example.servlet.mapper.BaristaDtoMapper;

import java.io.IOException;
import java.util.List;

@WebServlet(value = "/barista/*")
public class BaristaServlet extends SimpleServlet {
    private final transient BaristaService baristaService = new BaristaService(new TESTBaristaRepository(), new TestOrderRepository());
    private final transient BaristaDtoMapper mapper = new BaristaDtoMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("text/html");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<BaristaPublicDTO> baristaDtoList = baristaService.findAll().stream()
                    .map(BaristaPublicDTO::new)
                    .toList();

            String json = arrToJson(baristaDtoList, mapper);

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
            Barista barista = baristaService.findById(id);
            String json = mapper.map(new BaristaPublicDTO(barista));
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
            BaristaNoRefDTO baristaDTO = mapper.map(requestBody);

            Barista barista = baristaService.create(baristaDTO);

            String json = mapper.map(new BaristaPublicDTO(barista));
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

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        try {
            String requestBody = getRequestBody(req);
            BaristaNoRefDTO barista = mapper.map(requestBody);
            baristaService.update(barista);

        } catch (LessZeroException | NoValidIdException | InfiniteException | NaNException | NoValidNameException |
                 NullParamException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad param!");
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

            baristaService.delete(id);
        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
