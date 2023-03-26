package ru.vsu.csf.asashina.marketservice.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException() {
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
