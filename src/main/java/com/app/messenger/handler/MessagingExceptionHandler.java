package com.app.messenger.handler;

import com.app.messenger.handler.dto.ExceptionResponse;
import com.app.messenger.websocket.exception.*;
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

    @ExceptionHandler(value = {
            ChatMemberAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleChatMemberAlreadyExistsException() {
        return ExceptionResponse
                .builder()
                .message("Chat member is already in the chat")
                .build();
    }

    @ExceptionHandler(value = {
            ChatMemberNotFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleChatMemberNotFoundException() {
        return ExceptionResponse
                .builder()
                .message("Chat member not found")
                .build();
    }

    @ExceptionHandler(value = {
            ChatModificationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleChatModificationException() {
        return ExceptionResponse
                .builder()
                .message("Can not modify chat")
                .build();
    }
}
