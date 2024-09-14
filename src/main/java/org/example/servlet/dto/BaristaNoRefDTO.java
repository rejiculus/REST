package org.example.servlet.dto;

import org.example.entity.Barista;
import org.example.entity.Order;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.example.repository.OrderRepository;
import org.example.service.dto.IBaristaNoRefDTO;
import org.example.service.exception.OrderNotFoundException;

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

    /**
     * @param orderRepository
     * @return
     * @throws NullParamException      from IBaristaNoRefDTO
     * @throws NoValidIdException      from IBaristaNoRefDTO
     * @throws NoValidNameException    from IBaristaNoRefDTO
     * @throws NoValidTipSizeException from IBaristaNoRefDTO
     */
    @Override
    public Barista toBarista(OrderRepository orderRepository) {
        Barista barista = new Barista(fullName);

        if (id != null)
            barista.setId(id);
        if (tipSize != null)
            barista.setTipSize(tipSize);
        if (orderIdList != null && !orderIdList.isEmpty())
            barista.setOrderList(orderIdList.stream()
                    .map(orderId -> orderRepository.findById(orderId)
                            .orElseThrow(() -> new OrderNotFoundException(orderId)))
                    .toList());

        return barista;
    }
}
