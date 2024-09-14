package org.example.service.exception;

public class CoffeeAlreadyExistException extends RuntimeException {
    public CoffeeAlreadyExistException(Long id){
        super(String.format("Coffee with '%d' id is already exist!",id));
    }
}
