package com.app.messenger.websocket.repository.model;

public enum MessageType {
    CHAT_MEMBER_ADDED_TO_CHAT,
    CHAT_MEMBER_DELETED_FROM_CHAT,
    CHAT_MEMBER_LEFT_CHAT,
    NEW_MESSAGE,
    DELETED_MESSAGE,
    MODIFIED_MESSAGE,
    MODIFIED_CHAT,
    NEW_STATUS_IN_CHAT_MEMBER
}
