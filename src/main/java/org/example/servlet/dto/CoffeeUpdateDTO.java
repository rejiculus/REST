package org.example.servlet.dto;

import org.example.service.dto.ICoffeeUpdateDTO;

import java.util.List;

public record CoffeeUpdateDTO(Long id,
                              String name,
                              Double price,
                              List<Long> orderIdList)
        implements ICoffeeUpdateDTO {
}
