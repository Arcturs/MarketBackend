package ru.vsu.csf.asashina.market.exception;

public class ObjectAlreadyExistsException extends RuntimeException {

    public ObjectAlreadyExistsException() {
    }

    public ObjectAlreadyExistsException(String message) {
        super(message);
    }
}
