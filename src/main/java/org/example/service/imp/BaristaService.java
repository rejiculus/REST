package org.example.service.imp;

import org.example.entity.Barista;
import org.example.service.SimpleCrudService;
import org.example.repository.BaristaRepository;

import java.util.List;

public class BaristaService implements SimpleCrudService<Barista, Long> {
    private final BaristaRepository baristaRepository;

    public BaristaService(BaristaRepository baristaRepository) {
        this.baristaRepository = baristaRepository;
    }

    @Override
    public Barista create(Barista barista) {
        return this.baristaRepository.create(barista);
    }

    @Override
    public Barista update(Barista barista) {
        return this.baristaRepository.update(barista);
    }

    @Override
    public void delete(Long id) {
        this.baristaRepository.delete(id);

    }

    @Override
    public Barista findById(Long id) {
        return this.baristaRepository.findById(id);
    }

    @Override
    public List<Barista> findAll() {
        return this.baristaRepository.findAll();
    }
}
