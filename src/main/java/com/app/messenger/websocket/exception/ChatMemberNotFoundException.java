package com.app.messenger.websocket.exception;

public class ChatMemberNotFoundException extends Exception {
    public ChatMemberNotFoundException(String message) {
        super(message);
    }
}
