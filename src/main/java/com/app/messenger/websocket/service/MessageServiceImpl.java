package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.service.AuthenticationService;
import com.app.messenger.security.service.E2EEService;
import com.app.messenger.service.MessageConverter;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.exception.ChatNotFoundException;
import com.app.messenger.websocket.exception.MessageNotFoundException;
import com.app.messenger.websocket.repository.ChatMemberRepository;
import com.app.messenger.websocket.repository.ChatRepository;
import com.app.messenger.websocket.repository.MessageRepository;
import com.app.messenger.websocket.repository.MessageStatusRepository;
import com.app.messenger.websocket.repository.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import java.security.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageStatusRepository messageStatusRepository;
    private final MessageConverter messageConverter;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final AuthenticationService authenticationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final E2EEService e2eeService;

    @Override
    public Collection<MessageDto> getAllMessagesFromChat(String chatId) throws Exception {
        UUID chatUUID = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(chatUUID)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id: " + chatId + "does not exist")
                );
        List<Message> messages = messageRepository.findAllByChat(chat);
        List<MessageDto> messagesToReturn = new ArrayList<>();
        for (Message message : messages) {
            messagesToReturn.add(messageConverter.toDto(message));
        }
        return messagesToReturn;
    }

    @Override
    @Transactional
    public MessageDto sendMessageToChat(MessageDto messageDto) throws Exception {
        String decryptedText = e2eeService.decrypt(messageDto.getContent());
        messageDto.setContent(decryptedText);

        Message message = messageConverter.toEntity(messageDto);
        Message savedMessage = messageRepository.save(message);

        UUID senderId = savedMessage.getSender().getId();
        UUID chatId = savedMessage.getChat().getId();
        List<ChatMember> chatMembers = chatMemberRepository.findAllByChatId(chatId);
        List<MessageStatus> messageStatuses = new ArrayList<>();
        for (ChatMember chatMember : chatMembers) {
            User user = chatMember.getMember();
            UUID userId = user.getId();
            UserType userType = UserType.RECEIVER;
            if (userId.equals(senderId)) {
                userType = UserType.SENDER;
            }
            messageStatuses.add(MessageStatus
                    .builder()
                    .message(savedMessage)
                    .user(user)
                    .userType(userType)
                    .status(Status.UNREAD_MESSAGE)
                    .build());
        }
        messageStatusRepository.saveAll(messageStatuses);

        MessageDto messageToSend = messageConverter.toDto(savedMessage);
        String encryptedText = e2eeService.encrypt(messageToSend.getContent());
        messageToSend.setContent(encryptedText);
        processAndSendMessageToChat(messageToSend);

        return MessageDto
                .builder()
                .id(messageToSend.getId())
                .sender(messageToSend.getSender())
                .chatId(messageDto.getChatId())
                .content(messageToSend.getContent())
                .sendTime(messageToSend.getSendTime())
                .type(messageToSend.getType())
                .status(Status.UNREAD_MESSAGE)
                .build();
    }

    @Override
    @Transactional
    public MessageDto deleteMessageInChat(String chatId, String messageId) throws Exception {
        UUID convertedChatId = UUID.fromString(chatId);
        UUID convertedMessageId = UUID.fromString(messageId);

        User currentUser = authenticationService.getCurrentUser();
        MessageDto messageDtoToReturn = null;
        if (chatRepository.existsById(convertedChatId)) {
            Message message = messageRepository
                    .findById(convertedMessageId)
                    .orElseThrow(
                            () -> new MessageNotFoundException("Message with id " + messageId + " does not exist")
                    );
            if (message != null) {
                UUID returnedMessageChatId = message.getChat().getId();
                UUID returnedMessageSenderId = message.getSender().getId();
                if (returnedMessageSenderId.equals(currentUser.getId()) &&
                        returnedMessageChatId.equals(convertedChatId)) {
                    messageDtoToReturn = messageConverter.toDto(message);
                    String encryptedText = e2eeService.encrypt(messageDtoToReturn.getContent());
                    messageDtoToReturn.setContent(encryptedText);
                    messageDtoToReturn.setType(MessageType.DELETED_CHAT_MESSAGE);
                    messageRepository.deleteById(message.getId());
                    processAndSendMessageToChat(messageDtoToReturn);
                }
            }
        }

        return messageDtoToReturn;
    }

    @Override
    public Collection<MessageDto> encryptAllMessagesUsingE2EEWithCurrentUserPublicKey(Collection<MessageDto> messagesToEncrypt) throws Exception {
        for (MessageDto messageDto : messagesToEncrypt) {
            String content = messageDto.getContent();
            String encryptedContent = e2eeService.encrypt(content);
            messageDto.setContent(encryptedContent);
        }
        return messagesToEncrypt;
    }

    private void processAndSendMessageToChat(MessageDto messageDto) {
        UserDto sender = messageDto.getSender();
        String chatId = messageDto.getChatId();
        String messageId = messageDto.getId();
        String content = messageDto.getContent();
        String sendTime = messageDto.getSendTime();
        MessageType messageType = messageDto.getType();

        if (sender == null) {
            throw new IllegalArgumentException("Message sender is null");
        } else if (chatId == null) {
            throw new IllegalArgumentException("Message chatId in null");
        } else if (messageId == null) {
            throw new IllegalArgumentException("Message id is null");
        } else if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content is null or blank");
        } else if (sendTime == null) {
            throw new IllegalArgumentException("Message time is null");
        } else if (messageType == null) {
            throw new IllegalArgumentException("Message type is null");
        }

        simpMessagingTemplate.convertAndSend(
                "/api/messaging/topic/chats/" + chatId + "/messages",
                MessageDto
                        .builder()
                        .id(messageId)
                        .sender(sender)
                        .chatId(chatId)
                        .content(content)
                        .sendTime(sendTime)
                        .type(messageType)
                        .status(Status.UNREAD_MESSAGE)
                        .build()
        );
    }
}
