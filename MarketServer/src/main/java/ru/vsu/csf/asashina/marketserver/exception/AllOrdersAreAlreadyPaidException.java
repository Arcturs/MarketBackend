package ru.vsu.csf.asashina.marketserver.exception;

public class AllOrdersAreAlreadyPaidException extends RuntimeException {
    public AllOrdersAreAlreadyPaidException() {
    }

    public AllOrdersAreAlreadyPaidException(String message) {
        super(message);
    }
}
