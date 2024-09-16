package org.example.service.mapper;

import org.example.entity.Barista;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.example.repository.OrderRepository;
import org.example.repository.RepositoryFactory;
import org.example.service.dto.IBaristaNoRefDTO;
import org.example.service.exception.OrderNotFoundException;
import org.example.servlet.dto.BaristaNoRefDTO;


//map baristaNoRefDTO to Barista
public class BaristaDtoToBaristaMapper {
    private final OrderRepository orderRepository;

    public BaristaDtoToBaristaMapper() {
        orderRepository = RepositoryFactory.getOrderRepository();
    }

    public BaristaDtoToBaristaMapper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * @param baristaDTO
     * @return
     * @throws NullParamException
     * @throws NoValidIdException
     * @throws NoValidNameException
     * @throws NoValidTipSizeException
     */


    public Barista  map(IBaristaNoRefDTO baristaDTO) {
        Barista barista = new Barista(baristaDTO.fullName());

        if (baristaDTO.id() != null)
            barista.setId(baristaDTO.id());
        if (baristaDTO.tipSize() != null)
            barista.setTipSize(baristaDTO.tipSize());
        if (baristaDTO.orderIdList() != null && !baristaDTO.orderIdList().isEmpty())
            barista.setOrderList(baristaDTO.orderIdList().stream()
                    .map(orderId -> orderRepository.findById(orderId)
                            .orElseThrow(() -> new OrderNotFoundException(orderId)))
                    .toList());

        return barista;

    }
}
