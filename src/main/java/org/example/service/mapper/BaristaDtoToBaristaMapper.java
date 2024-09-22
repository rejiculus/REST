package org.example.service.mapper;

import org.example.entity.Barista;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.example.entity.exception.OrderNotFoundException;
import org.example.repository.OrderRepository;
import org.example.service.dto.IBaristaCreateDTO;
import org.example.service.dto.IBaristaUpdateDTO;


//map baristaNoRefDTO to Barista
public class BaristaDtoToBaristaMapper {
    private final OrderRepository orderRepository;

    public BaristaDtoToBaristaMapper(OrderRepository orderRepository) {
        if (orderRepository == null)
            throw new NullParamException();

        this.orderRepository = orderRepository;
    }

    /**
     * @param baristaDTO
     * @return
     * @throws NullParamException
     * @throws NoValidNameException
     * @throws NoValidTipSizeException
     */
    public Barista map(IBaristaCreateDTO baristaDTO) {
        if (baristaDTO == null)
            throw new NullParamException();

        Barista barista = new Barista(baristaDTO.fullName());

        if (baristaDTO.tipSize() != null)
            barista.setTipSize(baristaDTO.tipSize());

        return barista;
    }

    /**
     * @param baristaDTO
     * @return
     * @throws NullParamException
     * @throws NoValidNameException
     * @throws NoValidTipSizeException
     */
    public Barista map(IBaristaUpdateDTO baristaDTO) {
        if (baristaDTO == null)
            throw new NullParamException();


        return new Barista(
                baristaDTO.id(),
                baristaDTO.fullName(),
                baristaDTO.orderIdList().stream()
                        .map(orderId -> orderRepository.findById(orderId)
                                .orElseThrow(() -> new OrderNotFoundException(orderId)))
                        .toList(),
                baristaDTO.tipSize());
    }
}
