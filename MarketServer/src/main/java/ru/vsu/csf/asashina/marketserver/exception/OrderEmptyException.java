package ru.vsu.csf.asashina.marketserver.exception;

public class OrderEmptyException extends RuntimeException {
    public OrderEmptyException() {
    }

    public OrderEmptyException(String message) {
        super(message);
    }
}
