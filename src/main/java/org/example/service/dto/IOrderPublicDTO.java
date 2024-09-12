package org.example.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public interface IOrderPublicDTO {
    Long id();

    IBaristaNoRefDTO baristaId();

    LocalDateTime created();

    LocalDateTime completed();

    Double price();

    List<? extends ICoffeeNoRefDTO> coffeeIdList();

}
