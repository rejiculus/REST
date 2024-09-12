package org.example.servlet.dto;

import org.example.entity.Barista;
import org.example.service.dto.IBaristaPublicDTO;

import java.util.List;

public record BaristaPublicDTO(Long id,
                               String fullName,
                               Double tipSize,
                               List<OrderNoRefDTO> orders)
        implements IBaristaPublicDTO {

    public BaristaPublicDTO(Barista barista) {
        this(
                barista.getId(),
                barista.getFullName(),
                barista.getTipSize(),
                barista.getOrderList().stream()
                        .map(OrderNoRefDTO::new)
                        .toList()
        );
    }

}
