package com.app.messenger.handler;

import com.app.messenger.exception.DecompressionException;
import com.app.messenger.exception.InvalidImageTypeException;
import com.app.messenger.handler.dto.ExceptionResponse;
import io.jsonwebtoken.CompressionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            NullPointerException.class,
            InvalidImageTypeException.class,
            CompressionException.class,
            DecompressionException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleInvalidJwtException() {
        return ExceptionResponse
                .builder()
                .message("Invalid values")
                .build();
    }
}
