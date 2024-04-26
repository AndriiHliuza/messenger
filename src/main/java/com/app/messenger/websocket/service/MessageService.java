package com.app.messenger.websocket.service;

import com.app.messenger.websocket.controller.dto.ChatMessagesStatusUpdateDto;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.repository.model.Status;
import com.app.messenger.websocket.repository.model.UserType;

import java.util.Collection;
import java.util.UUID;

public interface MessageService {
    Collection<MessageDto> getAllMessagesFromChat(String chatId) throws Exception;
    MessageDto sendMessageToChat(MessageDto messageDto) throws Exception;
    MessageDto updateMessageInChat(String chatId, String messageId, MessageDto messageDto) throws Exception;
    MessageDto deleteMessageInChat(String chatId, String messageId) throws Exception;
    Collection<MessageDto> encryptAllMessagesUsingE2EEWithCurrentUserPublicKey(
            Collection<MessageDto> messagesToEncrypt
    ) throws Exception;
    void processAndSendMessageToChat(MessageDto messageDto);
    void createMessagesStatusesForUserInChat(
            String username,
            UserType userType,
            UUID chatId,
            Status status
    ) throws Exception;
    ChatMessagesStatusUpdateDto updateMessagesStatusesInChat(
            String chatId,
            ChatMessagesStatusUpdateDto chatMessagesStatusUpdateDto
    ) throws Exception;

    MessageDto getMessageStatusForUser(String chatId, String messageId, String username) throws Exception;
}
