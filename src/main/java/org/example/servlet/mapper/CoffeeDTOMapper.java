package org.example.servlet.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.servlet.dto.CoffeeNoRefDTO;
import org.example.servlet.dto.CoffeePublicDTO;

public class CoffeeDTOMapper implements IJsonToDtoMapper<CoffeeNoRefDTO>, IDtoToJsonMapper<CoffeePublicDTO> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public CoffeeNoRefDTO map(String requestBody) throws Throwable {
        try {
            return mapper.readValue(requestBody, CoffeeNoRefDTO.class);

        } catch (JsonMappingException e) {
            throw e.getCause();
        }
    }

    @Override
    public String map(CoffeePublicDTO coffee) {
        try {
            return mapper.writeValueAsString(coffee);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
