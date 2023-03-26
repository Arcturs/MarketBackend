package ru.vsu.csf.asashina.marketservice.exception;

public class LowBalanceException extends RuntimeException {
    public LowBalanceException() {
    }

    public LowBalanceException(String message) {
        super(message);
    }
}
