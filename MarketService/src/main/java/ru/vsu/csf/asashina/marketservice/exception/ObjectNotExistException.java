package ru.vsu.csf.asashina.marketservice.exception;

public class ObjectNotExistException extends RuntimeException{

    public ObjectNotExistException() {
    }

    public ObjectNotExistException(String message) {
        super(message);
    }
}
