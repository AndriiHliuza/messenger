package com.app.messenger.websocket.exception;

public class ChatMemberAlreadyExistsException extends Exception {
    public ChatMemberAlreadyExistsException(String message) {
        super(message);
    }
}
