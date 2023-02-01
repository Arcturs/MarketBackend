package ru.vsu.csf.asashina.marketserver.exception;

public class TokenValidationException extends RuntimeException{

    public TokenValidationException() {
    }

    public TokenValidationException(String message) {
        super(message);
    }
}
