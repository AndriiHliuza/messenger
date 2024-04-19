package com.app.messenger.handler;

import com.app.messenger.handler.dto.ExceptionResponse;
import com.app.messenger.websocket.exception.ChatNotFoundException;
import com.app.messenger.websocket.exception.MessageAccessException;
import com.app.messenger.websocket.exception.MessageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MessagingExceptionHandler {
    @ExceptionHandler(value = {
            ChatNotFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleChatNotFoundException() {
        return ExceptionResponse
                .builder()
                .message("Chat not found")
                .build();
    }

    @ExceptionHandler(value = {
            MessageNotFoundException.class,
            MessageAccessException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMessageAccessException() {
        return ExceptionResponse
                .builder()
                .message("Message not found")
                .build();
    }
}
