package ru.vsu.csf.asashina.marketservice.exception;

public class PageException extends RuntimeException{

    public PageException() {
        super();
    }

    public PageException(String message) {
        super(message);
    }
}
