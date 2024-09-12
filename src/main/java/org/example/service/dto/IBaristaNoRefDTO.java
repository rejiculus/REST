package org.example.service.dto;

import org.example.entity.Barista;
import org.example.repository.OrderRepository;

import java.util.List;

public interface IBaristaNoRefDTO {
    Long id();

    String fullName();

    Double tipSize();

    List<Long> orderIdList();

    Barista toBarista(OrderRepository orderRepository);
}
