package com.app.messenger.handler;

import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.handler.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(value = {
            UserNotFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleUserNotFoundException() {
        return ExceptionResponse
                .builder()
                .message("User not found")
                .build();
    }
}
