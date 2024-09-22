package org.example.service.imp;

import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.repository.BaristaRepository;
import org.example.repository.OrderRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.repository.imp.BaristaRepositoryImp;
import org.example.repository.imp.OrderRepositoryImp;
import org.example.service.dto.IBaristaCreateDTO;
import org.example.service.dto.IBaristaUpdateDTO;
import org.example.service.mapper.BaristaDtoToBaristaMapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class BaristaService {
    private final BaristaRepository baristaRepository;
    private final OrderRepository orderRepository;
    private final BaristaDtoToBaristaMapper mapper;


    public BaristaService(BaristaRepository baristaRepository, OrderRepository orderRepository) {
        if (baristaRepository == null || orderRepository == null)
            throw new NullParamException();

        this.baristaRepository = baristaRepository;
        this.orderRepository = orderRepository;
        this.mapper = new BaristaDtoToBaristaMapper(orderRepository);
    }

    public BaristaService(Connection connection) {
        if (connection == null)
            throw new NullParamException();

        this.baristaRepository = new BaristaRepositoryImp(connection);
        this.orderRepository = new OrderRepositoryImp(connection);
        this.mapper = new BaristaDtoToBaristaMapper(orderRepository);
    }


    /**
     * @param baristaDTO
     * @return
     * @throws NullParamException      from IBaristaNoRefDTO
     * @throws NoValidIdException      from IBaristaNoRefDTO
     * @throws NoValidNameException    from IBaristaNoRefDTO
     * @throws NoValidTipSizeException from IBaristaNoRefDTO
     */
    public Barista create(IBaristaCreateDTO baristaDTO) {
        if (baristaDTO == null)
            throw new NullParamException();

        Barista barista = mapper.map(baristaDTO);
        return this.baristaRepository.create(barista);
    }

    /**
     * @param baristaDTO
     * @return
     * @throws NullParamException      from IBaristaNoRefDTO
     * @throws NoValidIdException      from IBaristaNoRefDTO
     * @throws NoValidNameException    from IBaristaNoRefDTO
     * @throws NoValidTipSizeException from IBaristaNoRefDTO
     */
    public Barista update(IBaristaUpdateDTO baristaDTO) {
        if (baristaDTO == null)
            throw new NullParamException();

        Barista barista = mapper.map(baristaDTO);

        barista = this.baristaRepository.update(barista);

        List<Order> expectedOrderList = orderRepository.findByBaristaId(barista.getId());
        List<Order> actualOrderList = barista.getOrderList();
        List<Order> deletedOrders = new ArrayList<>(expectedOrderList);
        List<Order> addedOrders = new ArrayList<>(actualOrderList);
        deletedOrders.removeAll(actualOrderList);
        actualOrderList.removeAll(expectedOrderList);

        for (Order order : deletedOrders) {
            orderRepository.setBaristaDefault(order.getId());
        }
        for (Order order : addedOrders) {
            order.setBarista(barista);
            orderRepository.update(order);
        }
        return barista;
    }

    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        this.baristaRepository.delete(id);

        List<Order> orderList = orderRepository.findByBaristaId(id);

        for (Order order : orderList) {
            orderRepository.setBaristaDefault(order.getId());
        }
    }

    public Barista findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        Barista barista = this.baristaRepository.findById(id)
                .orElseThrow(() -> new BaristaNotFoundException(id));

        barista.setOrderList(orderRepository.findByBaristaId(barista.getId()));

        return barista;
    }

    public List<Barista> findAll() {
        List<Barista> baristaList = this.baristaRepository.findAll();
        for (Barista barista : baristaList) {
            barista.setOrderList(orderRepository.findByBaristaId(barista.getId()));
        }
        return baristaList;
    }

    public List<Barista> findAllByPage(int page, int limit) {
        if (page < 0)
            throw new NoValidPageException(page);
        if (limit <= 0)
            throw new NoValidLimitException(limit);

        List<Barista> baristaList = this.baristaRepository.findAllByPage(page, limit);
        for (Barista barista : baristaList) {
            barista.setOrderList(orderRepository.findByBaristaId(barista.getId()));
        }
        return baristaList;
    }

}
