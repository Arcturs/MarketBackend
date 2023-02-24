package ru.vsu.csf.asashina.marketserver.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.vsu.csf.asashina.marketserver.exception.*;
import ru.vsu.csf.asashina.marketserver.model.ResponseBuilder;
import ru.vsu.csf.asashina.marketserver.model.dto.ExceptionDTO;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> internalServerErrorHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseBuilder.build(INTERNAL_SERVER_ERROR, new ExceptionDTO("Internal server error"));
    }

    @ExceptionHandler({PageException.class, TypeMismatchException.class, PasswordsDoNotMatchException.class,
            WrongCredentialsException.class, AddZeroAmountProductToOrderException.class})
    public ResponseEntity<?> badRequestExceptionHandler(Exception e) {
        return ResponseBuilder.build(BAD_REQUEST, e);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(BindException e) {
        return ResponseBuilder.build(BAD_REQUEST,
                e.getBindingResult().getAllErrors().stream()
                        .map(error -> (FieldError) error)
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                DefaultMessageSourceResolvable::getDefaultMessage,
                                (message1, message2) -> message1 + ", " + message2
                        )));
    }

    @ExceptionHandler({TokenValidationException.class, TokenExpiredException.class})
    public ResponseEntity<?> unauthorizedExceptionHandler(Exception e) {
        log.error(e.getMessage());
        return ResponseBuilder.build(UNAUTHORIZED, e);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<?> forbiddenExceptionHandler(Exception e) {
        return ResponseBuilder.build(FORBIDDEN, e);
    }

    @ExceptionHandler({ObjectNotExistException.class})
    public ResponseEntity<?> notFoundExceptionHandler(Exception e) {
        return ResponseBuilder.build(NOT_FOUND, e);
    }

    @ExceptionHandler({OutOfStockException.class})
    public ResponseEntity<?> methodNotAllowedException(Exception e) {
        return ResponseBuilder.build(METHOD_NOT_ALLOWED, e);
    }

    @ExceptionHandler({ObjectAlreadyExistsException.class})
    public ResponseEntity<?> conflictExceptionHandler(Exception e) {
        return ResponseBuilder.build(CONFLICT, e);
    }
}
