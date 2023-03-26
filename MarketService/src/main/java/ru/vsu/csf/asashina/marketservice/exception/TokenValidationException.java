package ru.vsu.csf.asashina.marketservice.exception;

public class TokenValidationException extends RuntimeException{

    public TokenValidationException() {
    }

    public TokenValidationException(String message) {
        super(message);
    }
}
