package ru.vsu.csf.asashina.market.exception;

public class ObjectNotExistException extends RuntimeException{

    public ObjectNotExistException() {
    }

    public ObjectNotExistException(String message) {
        super(message);
    }
}
