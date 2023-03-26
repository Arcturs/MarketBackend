package ru.vsu.csf.asashina.marketservice.exception;

public class OrderEmptyException extends RuntimeException {
    public OrderEmptyException() {
    }

    public OrderEmptyException(String message) {
        super(message);
    }
}
