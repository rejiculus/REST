package org.example.servlet.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class DtoMapper<I, O> implements IJsonToDtoMapper<I>, IDtoToJsonMapper<O> {
    protected static ObjectMapper mapper = new ObjectMapper();

    static {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        mapper.registerModule(new JavaTimeModule()
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter)));

    }

}
