package ru.vsu.csf.asashina.marketserver.exception;

public class AddZeroAmountProductToOrderException extends RuntimeException{

    public AddZeroAmountProductToOrderException() {
    }

    public AddZeroAmountProductToOrderException(String message) {
        super(message);
    }
}
