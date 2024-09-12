package org.example.servlet.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.servlet.dto.BaristaNoRefDTO;
import org.example.servlet.dto.BaristaPublicDTO;

public class BaristaDtoMapper implements IJsonToDtoMapper<BaristaNoRefDTO>, IDtoToJsonMapper<BaristaPublicDTO> {
    ObjectMapper objectMapper = new ObjectMapper();

    public BaristaNoRefDTO map(String requestBody) throws Throwable {
        try {
            return objectMapper.readValue(requestBody, BaristaNoRefDTO.class);

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
    }

    public String map(BaristaPublicDTO barista) {
        try {
            return objectMapper.writeValueAsString(barista);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
