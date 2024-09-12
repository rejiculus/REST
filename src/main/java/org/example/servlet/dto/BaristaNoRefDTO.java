package org.example.servlet.dto;

import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.repository.OrderRepository;
import org.example.service.dto.IBaristaNoRefDTO;

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

    @Override
    public Barista toBarista(OrderRepository orderRepository) {
        return new Barista(
                id,
                fullName,
                orderIdList.stream().map(orderRepository::findById).toList(),
                tipSize
        );
    }
}
