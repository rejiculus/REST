package org.example.servlet.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.servlet.dto.OrderNoRefDTO;
import org.example.servlet.dto.OrderPublicDTO;

public class OrderDtoMapper implements IJsonToDtoMapper<OrderNoRefDTO>, IDtoToJsonMapper<OrderPublicDTO> {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public OrderNoRefDTO map(String requestBody) throws Throwable {
        try {
            return mapper.readValue(requestBody, OrderNoRefDTO.class);

        } catch (JsonMappingException e) {
            throw e.getCause();
        }
    }

    @Override
    public String map(OrderPublicDTO order) {
        try {
            return mapper.writeValueAsString(order);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
