package ru.vsu.csf.asashina.marketservice.exception;

public class WrongCredentialsException extends RuntimeException{

    public WrongCredentialsException() {
    }

    public WrongCredentialsException(String message) {
        super(message);
    }
}
