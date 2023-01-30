package ru.vsu.csf.asashina.marketserver.exception;

public class WrongCredentialsException extends RuntimeException{

    public WrongCredentialsException() {
    }

    public WrongCredentialsException(String message) {
        super(message);
    }
}
