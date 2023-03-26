package ru.vsu.csf.asashina.marketservice.exception;

public class AllOrdersAreAlreadyPaidException extends RuntimeException {
    public AllOrdersAreAlreadyPaidException() {
    }

    public AllOrdersAreAlreadyPaidException(String message) {
        super(message);
    }
}
