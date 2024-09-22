package org.example.servlet.dto;

import org.example.service.dto.IOrderCreateDTO;

import java.util.List;

public record OrderCreateDTO(Long baristaId,
                             List<Long> coffeeIdList)
        implements IOrderCreateDTO {
}
