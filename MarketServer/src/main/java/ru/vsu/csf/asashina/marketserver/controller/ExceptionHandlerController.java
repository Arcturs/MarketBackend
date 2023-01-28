package ru.vsu.csf.asashina.marketserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.vsu.csf.asashina.marketserver.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.exception.PageException;
import ru.vsu.csf.asashina.marketserver.exception.PasswordsDoNotMatchException;
import ru.vsu.csf.asashina.marketserver.model.ResponseBuilder;
import ru.vsu.csf.asashina.marketserver.model.dto.ExceptionDTO;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> internalServerErrorHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseBuilder.build(INTERNAL_SERVER_ERROR, new ExceptionDTO("Internal server error"));
    }

    @ExceptionHandler({PageException.class, TypeMismatchException.class, PasswordsDoNotMatchException.class})
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

    @ExceptionHandler({ObjectNotExistException.class})
    public ResponseEntity<?> notFoundExceptionHandler(Exception e) {
        return ResponseBuilder.build(NOT_FOUND, e);
    }

    @ExceptionHandler({ObjectAlreadyExistsException.class})
    public ResponseEntity<?> conflictExceptionHandler(Exception e) {
        return ResponseBuilder.build(CONFLICT, e);
    }
}
