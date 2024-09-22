package org.example.servlet.dto;

import org.example.service.dto.ICoffeeCreateDTO;

public record CoffeeCreateDTO(String name,
                              Double price)
        implements ICoffeeCreateDTO {
}
