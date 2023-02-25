package ru.vsu.csf.asashina.marketserver.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException() {
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
