package ru.vsu.csf.asashina.marketserver.exception;

public class LowBalanceException extends RuntimeException {
    public LowBalanceException() {
    }

    public LowBalanceException(String message) {
        super(message);
    }
}
