package org.example.service.implementation;

import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.entity.exception.*;
import org.example.service.IBaristaService;
import org.example.service.dto.IBaristaCreateDTO;
import org.example.service.dto.IBaristaUpdateDTO;
import org.example.service.exception.NoValidLimitException;
import org.example.service.exception.NoValidPageException;
import org.example.service.gateway.BaristaRepository;
import org.example.service.gateway.OrderRepository;
import org.example.service.mapper.BaristaDtoToBaristaMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for processing barista entity.
 */
public class BaristaService implements IBaristaService {
    private final BaristaRepository baristaRepository;
    private final OrderRepository orderRepository;
    private final BaristaDtoToBaristaMapper mapper;

    /**
     * Constructor based on repositories. Create mapper by order repository.
     *
     * @param baristaRepository repository to interact with barista schema in db.
     * @param orderRepository   repository to interact with order schema in db.
     * @throws NullParamException when some of params is null.
     */
    public BaristaService(BaristaRepository baristaRepository, OrderRepository orderRepository) {
        if (baristaRepository == null || orderRepository == null)
            throw new NullParamException();

        this.baristaRepository = baristaRepository;
        this.orderRepository = orderRepository;
        this.mapper = new BaristaDtoToBaristaMapper(orderRepository);
    }


    /**
     * Creating barista by IBaristaCreateDTO.
     *
     * @param baristaDTO object with IBaristaCreateDTO type.
     * @return Barista with specified id.
     * @throws NullParamException      when baristaDTO is null, or in mapper when some fields in baristaDTO is null.
     * @throws NoValidNameException    form mapper, when baristaDTO's fullName is empty.
     * @throws NoValidTipSizeException from mapper, when baristaDTO's tipSize is NaN, Infinite or less than zero.
     */
    @Override
    public Barista create(IBaristaCreateDTO baristaDTO) {
        if (baristaDTO == null)
            throw new NullParamException();

        Barista barista = mapper.map(baristaDTO);
        return this.baristaRepository.create(barista);
    }

    /**
     * Updating barista by IBaristaUpdateDTO.
     * Set default barista to orders that already not contains in orderIdList.
     * Adding reference with orders that already contains in orderIdList.
     *
     * @param baristaDTO object with IBaristaUpdateDTO type.
     * @return Barista object.
     * @throws NullParamException       when baristaDTO is null, or in mapper when some fields in baristaDTO is null.
     * @throws NoValidIdException       from mapper, when baristaDTO's id is less than zero.
     * @throws NoValidNameException     form mapper, when baristaDTO's fullName is empty.
     * @throws NoValidTipSizeException  from mapper, when baristaDTO's tipSize is NaN, Infinite or less than zero.
     * @throws BaristaNotFoundException from baristaRepository, when id is not found in db.
     * @throws OrderNotFoundException   from orderRepository, when order form baristaDTO's orderIdList is not found in db.
     */
    @Override
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

    /**
     * Delete barista from db.
     * Updating couped orders, setting default barista.
     *
     * @param id deleting barista.
     * @throws NullParamException       when id is null.
     * @throws NoValidIdException       when id is less than zero.
     * @throws BaristaNotFoundException when barista with this id is not found in db.
     */
    @Override
    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        List<Order> orderList = orderRepository.findByBaristaId(id);
        for (Order order : orderList) {
            orderRepository.setBaristaDefault(order.getId());
        }

        this.baristaRepository.delete(id);
    }

    /**
     * Found barista by id.
     *
     * @param id barista's id.
     * @return Barista's object.
     * @throws NullParamException       when id is null.
     * @throws NoValidIdException       when id is less than zero.
     * @throws BaristaNotFoundException when barista with this id is not found in db.
     */
    @Override
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

    /**
     * Found all barista.
     *
     * @return list of barista's objects
     */
    @Override
    public List<Barista> findAll() {
        List<Barista> baristaList = this.baristaRepository.findAll();
        for (Barista barista : baristaList) {
            barista.setOrderList(orderRepository.findByBaristaId(barista.getId()));
        }
        return baristaList;
    }

    /**
     * Found all barista, grouped by page and limited.
     *
     * @param page  number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of barista's objects
     * @throws NoValidPageException  when page is less than zero.
     * @throws NoValidLimitException when limit is less than one.
     */
    @Override
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
