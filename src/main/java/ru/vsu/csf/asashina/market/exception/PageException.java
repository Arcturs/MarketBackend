package ru.vsu.csf.asashina.market.exception;

public class PageException extends RuntimeException{

    public PageException() {
        super();
    }

    public PageException(String message) {
        super(message);
    }
}
