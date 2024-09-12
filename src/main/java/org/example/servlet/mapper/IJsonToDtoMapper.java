package org.example.servlet.mapper;

public interface IJsonToDtoMapper<T> {
    T map(String requestBody) throws Throwable;
}
