package org.example.servlet.dto;

import org.example.service.dto.IBaristaUpdateDTO;

import java.util.List;

public record BaristaUpdateDTO(Long id,
                               String fullName,
                               Double tipSize,
                               List<Long> orderIdList)
        implements IBaristaUpdateDTO {
}
