package com.app.messenger.service.converter;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.service.AuthenticationService;
import com.app.messenger.security.service.EncryptionService;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.exception.ChatNotFoundException;
import com.app.messenger.websocket.exception.MessageAccessException;
import com.app.messenger.websocket.repository.ChatRepository;
import com.app.messenger.websocket.repository.MessageStatusRepository;
import com.app.messenger.websocket.repository.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageConverter implements Converter<MessageDto, Message> {
    private final ChatRepository chatRepository;
    private final UserConverter userConverter;
    private final AuthenticationService authenticationService;
    private final MessageStatusRepository messageStatusRepository;
    private final EncryptionService encryptionServiceImpl;
    @Override
    public MessageDto toDto(Message message) throws Exception {
        UUID messageId = message.getId();
        User currentUser = authenticationService.getCurrentUser();
        UUID currentUserId = currentUser.getId();
        String currentUserUsername = currentUser.getUsername();

        User sender = message.getSender();
        UserDto senderDto = userConverter.toDto(sender);
        String chatId = message.getChat().getId().toString();
        String decryptedContent = encryptionServiceImpl.decrypt(message.getContent());
        MessageStatus messageStatusForCurrentUser = messageStatusRepository
                .findByMessageIdAndUserId(messageId, currentUserId)
                .orElseThrow(
                        () -> new MessageAccessException(
                                "User with username " +
                                currentUserUsername + " do not have access to the message with id " +
                                messageId.toString()
                        )
                );
        Status status = messageStatusForCurrentUser.getStatus();
        return MessageDto
                .builder()
                .id(messageId.toString())
                .sender(senderDto)
                .chatId(chatId)
                .content(decryptedContent)
                .sendTime(message.getSendTime().toString())
                .type(message.getType())
                .status(status)
                .build();
    }

    @Override
    public Message toEntity(MessageDto messageDto) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        UUID currentUserId = currentUser.getId();
        String username = currentUser.getUsername();

        UUID chatId = UUID.fromString(messageDto.getChatId());
        Chat chat = chatRepository
                .findByChatIdAndChatMemberId(chatId, currentUserId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with name: " + chatId + "and user: " + username + " not found in database")
                );

        String encryptedContent = encryptionServiceImpl.encrypt(messageDto.getContent());

        MessageType messageType = messageDto.getType();
        if (messageDto.getContent() == null || messageDto.getContent().isBlank()) {
            throw new IllegalArgumentException("Illegal message content");
        }
        return Message
                .builder()
                .sender(currentUser)
                .chat(chat)
                .content(encryptedContent)
                .sendTime(ZonedDateTime.now())
                .type(messageType)
                .build();
    }
}
