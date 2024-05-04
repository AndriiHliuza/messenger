package com.app.messenger.exception;

public class UserAccountNotFoundException extends Exception {
    public UserAccountNotFoundException(String message) {
        super(message);
    }
}
