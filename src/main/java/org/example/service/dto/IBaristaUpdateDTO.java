package org.example.service.dto;

import java.util.List;

public interface IBaristaUpdateDTO extends IUpdateDTO {
    Long id();

    String fullName();

    Double tipSize();

    List<Long> orderIdList();
}
