package org.example.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SimpleServlet extends HttpServlet {

    protected <T> String arrToJson(List<T> arr, Gson mapper) {
        return "{" +
                arr.stream()
                        .map(mapper::toJson)
                        .collect(Collectors.joining(", ")) +
                "}";
    }
}
