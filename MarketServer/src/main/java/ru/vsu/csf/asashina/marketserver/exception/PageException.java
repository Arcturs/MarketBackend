package ru.vsu.csf.asashina.marketserver.exception;

public class PageException extends RuntimeException{

    public PageException() {
        super();
    }

    public PageException(String message) {
        super(message);
    }
}
