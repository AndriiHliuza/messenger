package com.app.messenger.handler;

import com.app.messenger.handler.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = {
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleInvalidJwtException() {
        return ExceptionResponse
                .builder()
                .message("Invalid values")
                .build();
    }
}
