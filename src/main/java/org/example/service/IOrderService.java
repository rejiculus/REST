package org.example.service;

import org.example.entity.Order;
import org.example.service.dto.IOrderCreateDTO;
import org.example.service.dto.IOrderUpdateDTO;

import java.util.List;

public interface IOrderService {

    Order create(IOrderCreateDTO orderDTO);

    Order update(IOrderUpdateDTO orderDTO);

    void delete(Long id);

    List<Order> getOrderQueue();

    Order completeOrder(Long id);

    List<Order> findAll();

    Order findById(Long id);

    List<Order> findAllByPage(int page, int limit);

}
