package org.example.servlet.dto;

import org.example.service.dto.IOrderUpdateDTO;

import java.time.LocalDateTime;
import java.util.List;

public record OrderUpdateDTO(Long id,
                             Long baristaId,
                             LocalDateTime created,
                             LocalDateTime completed,
                             Double price,
                             List<Long> coffeeIdList)
        implements IOrderUpdateDTO {
}
