package org.example.servlet.dto;

import org.example.service.dto.IBaristaCreateDTO;

public record BaristaCreateDTO(String fullName,
                               Double tipSize
) implements IBaristaCreateDTO {
}
