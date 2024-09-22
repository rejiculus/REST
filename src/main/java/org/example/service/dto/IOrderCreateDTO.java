package org.example.service.dto;

import java.util.List;

public interface IOrderCreateDTO extends ICreateDTO {

    Long baristaId();

    List<Long> coffeeIdList();
}
