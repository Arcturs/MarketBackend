package ru.vsu.csf.asashina.marketservice.exception;

public class AddZeroAmountProductToOrderException extends RuntimeException{

    public AddZeroAmountProductToOrderException() {
    }

    public AddZeroAmountProductToOrderException(String message) {
        super(message);
    }
}
