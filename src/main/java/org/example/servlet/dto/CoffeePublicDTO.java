package org.example.servlet.dto;

import org.example.entity.Coffee;
import org.example.service.dto.ICoffeePublicDTO;

import java.util.List;

public record CoffeePublicDTO(Long id,
                              String name,
                              Double price,
                              List<OrderNoRefDTO> orders)
        implements ICoffeePublicDTO {

    public CoffeePublicDTO(Coffee coffee) {
        this(
                coffee.getId(),
                coffee.getName(),
                coffee.getPrice(),
                coffee.getOrderList().stream()
                        .map(OrderNoRefDTO::new)
                        .toList()
        );
    }
}
