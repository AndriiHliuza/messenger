package com.app.messenger.websocket.service;

import com.app.messenger.security.exception.UserNotAuthenticatedException;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.exception.ChatNotFoundException;

import java.util.Collection;
import java.util.UUID;

public interface MessageService {
    Collection<MessageDto> getAllMessagesFromChat(String chatId) throws Exception;
    MessageDto sendMessageToChat(MessageDto messageDto) throws Exception;
    MessageDto deleteMessageInChat(String chatId, String messageId) throws Exception;
    Collection<MessageDto> encryptAllMessagesUsingE2EEWithCurrentUserPublicKey(Collection<MessageDto> messagesToEncrypt) throws Exception;
}
