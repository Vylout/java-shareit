package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.rmi.AccessException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ErrorResponse handleNotValidArgumentException(Exception e) {
        log.warn(e.getClass().getSimpleName(), e);
        String message;
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException eValidation = (MethodArgumentNotValidException) e;
            message = Objects.requireNonNull(eValidation.getBindingResult().getFieldError()).getDefaultMessage();
        } else {
            message = e.getMessage();
        }
        return new ErrorResponse(400, "Bad Request", message);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({AccessException.class})
    public ErrorResponse handleDataExistExceptionException(RuntimeException e) {
        log.warn(e.getClass().getSimpleName(), e);
        return new ErrorResponse(404, "Not Found", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ArgumentException.class})
    public ErrorResponse handleArgumentExceptionHandler(RuntimeException e) {
        log.warn(e.getClass().getSimpleName(), e);
        return new ErrorResponse(400, "Bad Request", e.getMessage());
    }
}
