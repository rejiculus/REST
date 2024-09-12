package org.example.service.imp;

import org.example.entity.Barista;
import org.example.repository.BaristaRepository;
import org.example.repository.OrderRepository;
import org.example.service.dto.IBaristaNoRefDTO;

import java.util.List;

public class BaristaService {
    private final BaristaRepository baristaRepository;
    private final OrderRepository orderRepository;


    public BaristaService(BaristaRepository baristaRepository, OrderRepository orderRepository) {
        this.baristaRepository = baristaRepository;
        this.orderRepository = orderRepository;
    }

    public Barista create(IBaristaNoRefDTO baristaDTO) {
        Barista barista = baristaDTO.toBarista(orderRepository);
        return this.baristaRepository.create(barista);
    }

    public Barista update(IBaristaNoRefDTO baristaDTO) {

        Barista barista = baristaDTO.toBarista(orderRepository);

        return this.baristaRepository.update(barista);
    }

    public void delete(Long id) {
        this.baristaRepository.delete(id);

    }

    public Barista findById(Long id) {
        return this.baristaRepository.findById(id);
    }

    public List<Barista> findAll() {
        return this.baristaRepository.findAll();
    }
}
