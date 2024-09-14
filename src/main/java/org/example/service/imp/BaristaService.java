package org.example.service.imp;

import org.example.entity.Barista;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepository;
import org.example.repository.OrderRepository;
import org.example.service.dto.IBaristaNoRefDTO;
import org.example.service.exception.BaristaNotFoundException;

import java.util.List;

public class BaristaService {
    private final BaristaRepository baristaRepository;
    private final OrderRepository orderRepository;


    public BaristaService(BaristaRepository baristaRepository, OrderRepository orderRepository) {
        this.baristaRepository = baristaRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * @param baristaDTO
     * @return
     * @throws NullParamException      from IBaristaNoRefDTO
     * @throws NoValidIdException      from IBaristaNoRefDTO
     * @throws NoValidNameException    from IBaristaNoRefDTO
     * @throws NoValidTipSizeException from IBaristaNoRefDTO
     */
    public Barista create(IBaristaNoRefDTO baristaDTO) {
        if (baristaDTO == null)
            throw new NullParamException();
        Barista barista = baristaDTO.toBarista(orderRepository);
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
    public Barista update(IBaristaNoRefDTO baristaDTO) {
        if (baristaDTO == null)
            throw new NullParamException();

        Barista barista = baristaDTO.toBarista(orderRepository);

        return this.baristaRepository.update(barista);
    }

    public void delete(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        this.baristaRepository.delete(id);

    }

    public Barista findById(Long id) {
        if (id == null)
            throw new NullParamException();
        if (id < 0)
            throw new NoValidIdException(id);

        return this.baristaRepository.findById(id)
                .orElseThrow(() -> new BaristaNotFoundException(id));
    }

    public List<Barista> findAll() {
        return this.baristaRepository.findAll();
    }
}
