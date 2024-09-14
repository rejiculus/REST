package org.example.servlet.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.example.servlet.dto.OrderNoRefDTO;
import org.example.servlet.dto.OrderPublicDTO;

public class OrderDtoMapper extends DtoMapper<OrderNoRefDTO, OrderPublicDTO> {

    @Override
    public OrderNoRefDTO map(String requestBody) throws Throwable {
        try {
            return mapper.readValue(requestBody, OrderNoRefDTO.class);

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
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
