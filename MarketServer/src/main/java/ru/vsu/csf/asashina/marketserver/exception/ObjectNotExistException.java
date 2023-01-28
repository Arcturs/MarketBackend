package ru.vsu.csf.asashina.marketserver.exception;

public class ObjectNotExistException extends RuntimeException{

    public ObjectNotExistException() {
    }

    public ObjectNotExistException(String message) {
        super(message);
    }
}
