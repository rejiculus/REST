package org.example.service;

import org.example.entity.Barista;
import org.example.service.dto.IBaristaCreateDTO;
import org.example.service.dto.IBaristaUpdateDTO;

import java.util.List;

public interface IBaristaService {
    /**
     * Creating barista by IBaristaCreateDTO.
     * @param baristaDTO object with IBaristaCreateDTO type.
     * @return Barista with specified id.
     */
    Barista create(IBaristaCreateDTO baristaDTO);

    /**
     * Updating barista by IBaristaUpdateDTO.
     * Set default barista to orders that already not contains in orderIdList.
     * Adding reference with orders that already contains in orderIdList.
     * @param baristaDTO object with IBaristaUpdateDTO type.
     * @return Barista object.
     */
    Barista update(IBaristaUpdateDTO baristaDTO);

    /**
     * Delete barista from db.
     * Updating couped orders, setting default barista.
     * @param id deleting barista.
     */
    void delete(Long id);

    /**
     * Found barista by id.
     * @param id barista's id.
     * @return Barista's object.
     */
    Barista findById(Long id);

    /**
     * Found all barista.
     * @return list of barista's objects
     */
    List<Barista> findAll();

    /**
     * Found all barista, grouped by page and limited.
     * @param page number of page. Can't be less than zero.
     * @param limit number of maximum objects in list.
     * @return list of barista's objects
     */
    List<Barista> findAllByPage(int page, int limit);
}
