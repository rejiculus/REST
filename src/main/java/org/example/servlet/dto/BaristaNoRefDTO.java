package org.example.servlet.dto;

import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.example.repository.OrderRepository;
import org.example.service.dto.IBaristaNoRefDTO;
import org.example.service.exception.OrderNotFoundException;

import java.util.List;

public record BaristaNoRefDTO(Long id,
                              String fullName,
                              Double tipSize,
                              List<Long> orderIdList) implements IBaristaNoRefDTO {

    public BaristaNoRefDTO(Barista barista) {
        this(
                barista.getId(),
                barista.getFullName(),
                barista.getTipSize(),
                barista.getOrderList().stream()
                        .map(Order::getId)
                        .toList()
        );
    }

}
