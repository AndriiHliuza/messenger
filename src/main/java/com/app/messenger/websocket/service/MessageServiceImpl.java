package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.exception.E2EEKeyNotFoundException;
import com.app.messenger.security.service.AuthenticationService;
import com.app.messenger.security.service.E2EEService;
import com.app.messenger.security.service.EncryptionService;
import com.app.messenger.service.MessageConverter;
import com.app.messenger.service.UserConverter;
import com.app.messenger.websocket.controller.dto.ChatDto;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;
import com.app.messenger.websocket.controller.dto.ChatMessagesStatusUpdateDto;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.exception.ChatMemberNotFoundException;
import com.app.messenger.websocket.exception.ChatNotFoundException;
import com.app.messenger.websocket.exception.MessageNotFoundException;
import com.app.messenger.websocket.repository.ChatMemberRepository;
import com.app.messenger.websocket.repository.ChatRepository;
import com.app.messenger.websocket.repository.MessageRepository;
import com.app.messenger.websocket.repository.MessageStatusRepository;
import com.app.messenger.websocket.repository.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final MessageRepository messageRepository;
    private final MessageStatusRepository messageStatusRepository;
    private final MessageConverter messageConverter;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final AuthenticationService authenticationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EncryptionService encryptionService;
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
//    @Transactional
    public MessageDto sendMessageToChat(MessageDto messageDto) throws Exception {
        if (!messageDto.getType().equals(MessageType.NEW_MESSAGE)) {
            throw new IllegalArgumentException("Invalid message type");
        }

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
    public MessageDto updateMessageInChat(String chatId, String messageId, MessageDto messageDto) throws Exception {
        MessageDto messageDtoToReturn = null;
        if (!messageDto.getType().equals(MessageType.MODIFIED_MESSAGE)) {
            throw new IllegalArgumentException("Illegal message type");
        }
        UUID convertedChatId = UUID.fromString(chatId);
        UUID convertedMessageId = UUID.fromString(messageId);

        if (!chatRepository.existsById(convertedChatId)) {
            throw new ChatNotFoundException("Chat with id " + chatId + " not found");
        }

        User currentUser = authenticationService.getCurrentUser();
        Message message = messageRepository
                .findByIdAndSenderIdAndChatId(convertedMessageId, currentUser.getId(), convertedChatId)
                .orElseThrow(
                        () -> new MessageNotFoundException("Message with id " + messageId
                                + " and sender id " + currentUser.getId()
                                + " not found in chat with id " + chatId)
                );

        String decryptedText = e2eeService.decrypt(messageDto.getContent());
        String encryptedContent = encryptionService.encrypt(decryptedText);
        String previousEncryptedContent = message.getContent();
        if (encryptedContent.equals(previousEncryptedContent)) {
            throw new IllegalArgumentException("New message content is equal to the previously saved message content");
        }
        message.setContent(encryptedContent);
        Message savedMessage = messageRepository.save(message);
        messageDtoToReturn = messageConverter.toDto(savedMessage);
        String encryptedText = e2eeService.encrypt(messageDtoToReturn.getContent());
        messageDtoToReturn.setContent(encryptedText);
        processAndSendMessageToChat(messageDtoToReturn);

        return messageDtoToReturn;
    }

    @Override
//    @Transactional
    public MessageDto deleteMessageFromChat(String chatId, String messageId) throws Exception {
        MessageDto messageDtoToReturn = null;

        UUID convertedChatId = UUID.fromString(chatId);
        UUID convertedMessageId = UUID.fromString(messageId);

        if (!chatRepository.existsById(convertedChatId)) {
            throw new ChatNotFoundException("Chat with id " + chatId + " not found");
        }

        Message message = messageRepository
                .findByIdAndChatId(convertedMessageId, convertedChatId)
                .orElseThrow(
                        () -> new MessageNotFoundException("Message with id " + messageId
                                + " not found in chat with id " + chatId)
                );
        User messageSender = message.getSender();

        User currentUser = authenticationService.getCurrentUser();
        ChatMember chatMember = chatMemberRepository
                .findByChatIdAndMemberUsername(convertedChatId, currentUser.getUsername())
                .orElseThrow(
                        () -> new ChatMemberNotFoundException("Chat member with member id " + currentUser.getId()
                                + " not found in chat with id " + chatId)
                );

        if (chatMember.getMemberRole().equals(MemberRole.ADMIN) || currentUser.getId().equals(messageSender.getId())) {
            messageDtoToReturn = messageConverter.toDto(message);
            String encryptedText = e2eeService.encrypt(messageDtoToReturn.getContent());
            messageDtoToReturn.setContent(encryptedText);
            messageDtoToReturn.setType(MessageType.DELETED_MESSAGE);
            messageRepository.deleteById(message.getId());
            processAndSendMessageToChat(messageDtoToReturn);
        }

        return messageDtoToReturn;
    }

    @Override
    public Collection<MessageDto> encryptAllMessagesUsingE2EEWithCurrentUserPublicKey(Collection<MessageDto> messagesToEncrypt) throws Exception {
        try {
            for (MessageDto messageDto : messagesToEncrypt) {
                String content = messageDto.getContent();
                String encryptedContent = e2eeService.encrypt(content);
                messageDto.setContent(encryptedContent);
            }
        } catch (E2EEKeyNotFoundException e) {
            messagesToEncrypt.forEach(messageDto -> messageDto.setContent(null));
        }

        return messagesToEncrypt;
    }

    @Override
    public void processAndSendMessageToChat(MessageDto messageDto) {
        String chatId = messageDto.getChatId();
        simpMessagingTemplate.convertAndSend(
                "/api/messaging/topic/chats/" + chatId + "/messages",
                MessageDto
                        .builder()
                        .id(messageDto.getId())
                        .sender(messageDto.getSender())
                        .chatId(chatId)
                        .content(messageDto.getContent())
                        .sendTime(messageDto.getSendTime())
                        .type(messageDto.getType())
                        .status(Status.UNREAD_MESSAGE)
                        .build()
        );
    }

    @Override
    public void createMessagesStatusesForUserInChat(
            String username,
            UserType userType,
            UUID chatId,
            Status status
    ) throws Exception {

        if (userType == null || status == null) {
            throw new IllegalArgumentException("User type or message status are null");
        }

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + username + " not found")
                );

        Chat chat = chatRepository
                .findById(chatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id " + chatId + " not found")
                );

        List<MessageStatus> unreadMessageStatusesForUserInChat = messageStatusRepository.findAllByChatIdAndUserIdAndStatus(
                chat.getId(),
                user.getId(),
                Status.UNREAD_MESSAGE
        );

        for (MessageStatus messageStatus : unreadMessageStatusesForUserInChat) {
            messageStatus.setStatus(Status.READ_MESSAGE);
        }
        messageStatusRepository.saveAll(unreadMessageStatusesForUserInChat);

//        List<Message> messages = messageRepository.findAllByChat(chat);
        List<Message> messagesWithNoStatusForUser = messageRepository.findAllMessagesByChatIdThatWereNotSentToUserWithProvidedId(
                chat.getId(),
                user.getId()
        );
        List<MessageStatus> messageStatuses = messagesWithNoStatusForUser
                .stream()
                .map(message -> MessageStatus
                        .builder()
                        .message(message)
                        .user(user)
                        .userType(userType)
                        .status(status)
                        .build())
                .toList();
        if (!messageStatuses.isEmpty()) {
            messageStatusRepository.saveAll(messageStatuses);
        }
    }

    @Override
//    @Transactional
    public ChatMessagesStatusUpdateDto updateMessagesStatusesInChat(
            String chatId,
            ChatMessagesStatusUpdateDto chatMessagesStatusUpdateDto
    ) throws Exception {
        if (!chatMessagesStatusUpdateDto.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Chat ids are not equal");
        }

        if (chatMessagesStatusUpdateDto.getStatus() == null) {
            throw new IllegalArgumentException("Status is null");
        }

        if (chatMessagesStatusUpdateDto.getStatus().equals(Status.UNREAD_MESSAGE)) {
            throw new IllegalArgumentException("Can not set status to UNREAD_MESSAGE");
        }

        UUID convertedChatId = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(convertedChatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id " + chatId + " not found")
                );

        Status status = chatMessagesStatusUpdateDto.getStatus();
        List<MessageStatus> messageStatuses = messageStatusRepository.findAllByChatIdAndStatus(
                chat.getId(),
                Status.UNREAD_MESSAGE
        );

        List<MessageStatus> messageStatusesToUpdate = new ArrayList<>();
        User currentUser = authenticationService.getCurrentUser();
        UUID currentUserId = currentUser.getId();
        for (MessageStatus messageStatus : messageStatuses) {
            User user = messageStatus.getUser();
            UserType userType = messageStatus.getUserType();
            if ((userType.equals(UserType.SENDER) && !user.getId().equals(currentUserId))
                    || (userType.equals(UserType.RECEIVER) && user.getId().equals(currentUserId))) {
                messageStatus.setStatus(status);
                messageStatusesToUpdate.add(messageStatus);
            }
        }
        messageStatusRepository.saveAll(messageStatusesToUpdate);

        return ChatMessagesStatusUpdateDto
                .builder()
                .chatId(chat.getId().toString())
                .status(Status.READ_MESSAGE)
                .build();
    }

    @Override
    public MessageDto getMessageStatusForUser(String chatId, String messageId, String username) throws Exception {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User with username " + username + " not found")
                );

        UUID convertedChatId = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(convertedChatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with chatId " + chatId + " not found")
                );

        UUID convertedMessageId = UUID.fromString(messageId);
        if (!messageRepository.existsByIdAndChatId(convertedMessageId, chat.getId())) {
            throw new MessageNotFoundException("Message with id " + messageId + " not found in chat with id " + chatId);
        }

        MessageStatus messageStatus = messageStatusRepository
                .findByMessageIdAndUserId(convertedMessageId, user.getId())
                .orElseThrow(
                        () -> new MessageNotFoundException("Message with id " + messageId + " not found for user with id " + user.getId())
                );

        return messageConverter.toDto(messageStatus.getMessage());
    }

    @Override
    public void sendMessageToChatOnChatUpdate(ChatDto updatedChat) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        UserDto currentUserDto = userConverter.toDto(currentUser);
        processAndSendMessageToChat(MessageDto
                .builder()
                .sender(currentUserDto)
                .chatId(updatedChat.getId())
                .content(MessageType.MODIFIED_CHAT.name())
                .sendTime(ZonedDateTime.now().toString())
                .type(MessageType.MODIFIED_CHAT)
                .status(Status.UNREAD_MESSAGE)
                .build());
    }

    @Override
    public void sendMessageToChatOnActionOnUserInChat(ChatMemberDto user, MessageType messageType) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        UserDto currentUserDto = userConverter.toDto(currentUser);
        processAndSendMessageToChat(MessageDto
                .builder()
                .sender(currentUserDto)
                .chatId(user.getChatId())
                .content(messageType.name())
                .sendTime(ZonedDateTime.now().toString())
                .type(messageType)
                .status(Status.UNREAD_MESSAGE)
                .build());
    }

    @Override
    public void sendMessageToChatOnUserDeletion(UserDto userDto) {
        List<Chat> userChats = chatRepository.findAllChatsByChatMemberUsername(userDto.getUsername());
        userChats.forEach(chat -> processAndSendMessageToChat(MessageDto
                .builder()
                .sender(userDto)
                .chatId(chat.getId().toString())
                .content(MessageType.CHAT_MEMBER_LEFT_CHAT.name())
                .sendTime(ZonedDateTime.now().toString())
                .type(MessageType.CHAT_MEMBER_LEFT_CHAT)
                .status(Status.UNREAD_MESSAGE)
                .build()));

    }
}
