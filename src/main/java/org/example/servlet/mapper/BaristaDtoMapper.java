package org.example.servlet.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.example.servlet.dto.BaristaNoRefDTO;
import org.example.servlet.dto.BaristaPublicDTO;

public class BaristaDtoMapper extends DtoMapper<BaristaNoRefDTO, BaristaPublicDTO> {

    @Override
    public BaristaNoRefDTO map(String requestBody) throws Throwable {
        try {
            return mapper.readValue(requestBody, BaristaNoRefDTO.class);

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String map(BaristaPublicDTO barista) {
        try {
            return mapper.writeValueAsString(barista);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
