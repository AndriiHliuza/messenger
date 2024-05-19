package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.service.AuthenticationService;
import com.app.messenger.service.converter.UserConverter;
import com.app.messenger.websocket.controller.dto.ChatDto;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;
import com.app.messenger.websocket.controller.dto.notifications.ChatNotificationDto;
import com.app.messenger.websocket.controller.dto.notifications.MultiUserNotificationDto;
import com.app.messenger.websocket.controller.dto.notifications.NotificationDto;
import com.app.messenger.websocket.repository.model.ChatType;
import com.app.messenger.websocket.repository.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthenticationService authenticationService;
    private final UserConverter userConverter;
    private final UserRepository userRepository;


    @Override
    @Async
    public void processAndSendNotificationToUser(NotificationDto notificationDto) {
        NotificationDto notificationDtoToBeSendToUser = processAndBuildNotificationDtoToBeSendToUser(notificationDto);

        if (notificationDtoToBeSendToUser != null) {
            String username = notificationDtoToBeSendToUser.getReceiverUsername();
            if (username != null) {
                simpMessagingTemplate.convertAndSend(
                        "/api/messaging/topic/" + username + "/notifications",
                        notificationDtoToBeSendToUser
                );
            }
        }
    }

    @Override
    @Async
    public void processAndSendNotificationToUsers(NotificationDto notificationDto, Collection<UserDto> usersToNotify) {
        if (notificationDto != null) {
            for (UserDto userToNotify : usersToNotify) {
                notificationDto.setReceiverUsername(userToNotify.getUsername());
                processAndSendNotificationToUser(notificationDto);
            }
        }
    }

    @Override
    @Async
    public void processAndSendMultiUserNotification(MultiUserNotificationDto multiUserNotificationDto) {
        NotificationDto notificationDto = multiUserNotificationDto.getNotification();
        List<UserDto> usersToNotify = multiUserNotificationDto.getUsersToNotify();
        processAndSendNotificationToUsers(notificationDto, usersToNotify);
    }

    @Override
    public void notifyUsersOnChatDeletion(ChatDto deletedChat) throws Exception {
        List<UserDto> membersToNotify = deletedChat
                .getMembers()
                .stream()
                .map(ChatMemberDto::getUser)
                .toList();

        User currentUser = authenticationService.getCurrentUser();
        UserDto currentUserDto = userConverter.toDto(currentUser);

        ChatNotificationDto chatNotificationDto = new ChatNotificationDto(
                currentUserDto.getUsername(),
                null,
                deletedChat.getName(),
                null,
                null,
                deletedChat.getId(),
                deletedChat.getName(),
                deletedChat.getType()
        );

        ChatType chatType = ChatType.valueOf(deletedChat.getType().toUpperCase());

        switch (chatType) {
            case PRIVATE_CHAT -> {
                UserDto anotherUserInPrivateChat = membersToNotify
                        .stream()
                        .filter(userDto -> !userDto.getUsername().equals(currentUserDto.getUsername()))
                        .findFirst()
                        .orElse(null);

                String anotherUserInPrivateChatUsername = null;

                if (anotherUserInPrivateChat != null) {
                    anotherUserInPrivateChatUsername = anotherUserInPrivateChat.getUsername();
                }

                chatNotificationDto.setType(NotificationType.DELETED_PRIVATE_CHAT_NOTIFICATION.name());
                processAndSendNotificationToUser(chatNotificationDto.cloneAndSetReceiverUsernameAndChatName(
                        currentUser.getUsername(),
                        anotherUserInPrivateChatUsername
                ));

                if (anotherUserInPrivateChatUsername != null) {
                    processAndSendNotificationToUser(chatNotificationDto.cloneAndSetReceiverUsernameAndChatName(
                            anotherUserInPrivateChatUsername,
                            currentUser.getUsername())
                    );
                }
            }
            case GROUP_CHAT -> {
                chatNotificationDto.setType(NotificationType.DELETED_GROUP_CHAT_NOTIFICATION.name());
                processAndSendNotificationToUsers(chatNotificationDto.clone(), membersToNotify);
            }
        }
    }

    @Override
    public void notifyUsersOnChatCreation(ChatDto createdChat) throws Exception {
        User currentUser = authenticationService.getCurrentUser();

        List<UserDto> membersToNotify = createdChat
                .getMembers()
                .stream()
                .map(ChatMemberDto::getUser)
                .toList();

        processAndSendNotificationToUsers(
                new ChatNotificationDto(
                        currentUser.getUsername(),
                        null,
                        createdChat.getName(),
                        null,
                        NotificationType.CREATED_NEW_CHAT_NOTIFICATION.name(),
                        createdChat.getId(),
                        createdChat.getName(),
                        createdChat.getType()
                ),
                membersToNotify
        );
    }

    @Override
    public void notifyChatMemberOnActionOnChatMemberInChat(
            ChatDto chatDto,
            ChatMemberDto chatMemberDto,
            NotificationType notificationType
    ) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        processAndSendNotificationToUser(new ChatNotificationDto(
                currentUser.getUsername(),
                chatMemberDto.getUser().getUsername(),
                chatDto.getName(),
                null,
                notificationType.name(),
                chatDto.getId(),
                chatDto.getName(),
                chatDto.getType()
        ));
    }

    private NotificationDto processAndBuildNotificationDtoToBeSendToUser(NotificationDto notificationDto) {
        try {
            if (notificationDto == null) {
                throw new IllegalArgumentException("Notification is null");
            }
            String notificationSenderUsername = notificationDto.getSenderUsername();
            User notificationSender = userRepository
                    .findByUsername(notificationSenderUsername)
                    .orElseThrow(
                            () -> new UserNotFoundException("User with username " + notificationSenderUsername + " not found")
                    );
            String senderUsername = notificationSender.getUsername();

            String notificationReceiverUsername = notificationDto.getReceiverUsername();
            User notificationReceiver = userRepository
                    .findByUsername(notificationDto.getReceiverUsername())
                    .orElseThrow(
                            () -> new UserNotFoundException("User with username " + notificationReceiverUsername + " not found")
                    );
            String receiverUsername = notificationReceiver.getUsername();

            String time = ZonedDateTime.now().toString();
            NotificationType type = NotificationType.valueOf(notificationDto.getType().toUpperCase());

            NotificationDto notificationDtoToReturn;
            if (notificationDto instanceof ChatNotificationDto chatNotificationDto
                    && (type.equals(NotificationType.CREATED_NEW_CHAT_NOTIFICATION)
                    || type.equals(NotificationType.DELETED_GROUP_CHAT_NOTIFICATION)
                    || type.equals(NotificationType.DELETED_PRIVATE_CHAT_NOTIFICATION)
                    || type.equals(NotificationType.NEW_ADMIN_IN_CHAT_NOTIFICATION)
                    || type.equals(NotificationType.USER_ADDED_TO_CHAT_NOTIFICATION)
                    || type.equals(NotificationType.DELETED_CHAT_MEMBER_NOTIFICATION)
                    || type.equals(NotificationType.MEMBER_LEFT_CHAT_NOTIFICATION))) {
                notificationDtoToReturn = processAndBuildChatNotificationDtoToBeSendToUser(chatNotificationDto);
            } else {
                notificationDtoToReturn = NotificationDto
                        .builder()
                        .senderUsername(senderUsername)
                        .receiverUsername(receiverUsername)
                        .content(notificationDto.getContent())
                        .time(time)
                        .type(type.name())
                        .build();
            }
            return notificationDtoToReturn;
        } catch (Exception ex) {
            log.error("Exception occurred while processing notification: " + ex.getMessage());
            return null;
        }
    }

    private ChatNotificationDto processAndBuildChatNotificationDtoToBeSendToUser(ChatNotificationDto chatNotificationDto) {
        String chatId = chatNotificationDto.getChatId();
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("Notification chatId is null");
        }
        String checkedChatId = UUID.fromString(chatId).toString();

        return new ChatNotificationDto(
                chatNotificationDto.getSenderUsername(),
                chatNotificationDto.getReceiverUsername(),
                chatNotificationDto.getContent(),
                chatNotificationDto.getTime(),
                chatNotificationDto.getType(),
                checkedChatId,
                chatNotificationDto.getChatName(),
                chatNotificationDto.getChatType()
        );
    }
}
