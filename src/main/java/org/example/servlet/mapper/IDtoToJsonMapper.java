package org.example.servlet.mapper;

public interface IDtoToJsonMapper<T> {
    String map(T dto);
}
