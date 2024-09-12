package org.example.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servlet.mapper.IDtoToJsonMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SimpleServlet extends HttpServlet {

    protected void jsonWriter(HttpServletResponse response, String json) throws IOException {
        PrintWriter printWriter = response.getWriter();

        printWriter.write(json);

        printWriter.close();
    }

    protected String getRequestBody(HttpServletRequest request) throws IOException {
        return request.getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));

    }

    protected <T> String arrToJson(List<T> arr, IDtoToJsonMapper<T> mapper) {
        return "{" +
                arr.stream()
                        .map(mapper::map)
                        .collect(Collectors.joining(", ")) +
                "}";
    }
}
