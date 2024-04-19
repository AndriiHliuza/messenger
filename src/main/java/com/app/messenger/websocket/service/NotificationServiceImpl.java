package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.websocket.controller.dto.notifications.ChatNotificationDto;
import com.app.messenger.websocket.controller.dto.notifications.NotificationDto;
import com.app.messenger.websocket.repository.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;

    @Override
    public void processAndSendNotificationToUser(NotificationDto notificationDto) throws UserNotFoundException {
        NotificationDto notificationDtoToBeSendToUser = processAndBuildNotificationDtoToBeSendToUser(notificationDto);

        if (notificationDtoToBeSendToUser != null) {
            String username = notificationDtoToBeSendToUser.getReceiverUsername();
            simpMessagingTemplate.convertAndSend(
                    "/api/messaging/topic/" + username + "/notifications",
                    notificationDtoToBeSendToUser
            );
        }
    }

    @Override
    public void processAndSendNotificationToUsers(NotificationDto notificationDto, Collection<UserDto> usersToNotify) throws UserNotFoundException {
        for (UserDto userToNotify : usersToNotify) {
            notificationDto.setReceiverUsername(userToNotify.getUsername());
            processAndSendNotificationToUser(notificationDto);
        }
    }

    private NotificationDto processAndBuildNotificationDtoToBeSendToUser(NotificationDto notificationDto) throws UserNotFoundException {
        try {
            String notificationReceiverUsername = notificationDto.getReceiverUsername();
            User notificationReceiver = userRepository
                    .findByUsername(notificationDto.getReceiverUsername())
                    .orElseThrow(() -> new UserNotFoundException("User with username " + notificationReceiverUsername + " not found"));
            String username = notificationReceiver.getUsername();

            String notificationContent = notificationDto.getContent();
            if (notificationContent == null || notificationContent.isBlank()) {
                throw new IllegalArgumentException("Notification content is missing");
            }

            String time = ZonedDateTime.now().toString();
            NotificationType type = NotificationType.valueOf(notificationDto.getType().toUpperCase());

            NotificationDto notificationDtoToReturn;
            if (notificationDto instanceof ChatNotificationDto chatNotificationDto && type.equals(NotificationType.NEW_CHAT_NOTIFICATION)) {
                notificationDtoToReturn = processAndBuildChatNotificationDtoToBeSendToUser(chatNotificationDto);
            } else {
                notificationDtoToReturn = NotificationDto
                        .builder()
                        .receiverUsername(username)
                        .content(notificationContent)
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
                chatNotificationDto.getReceiverUsername(),
                chatNotificationDto.getContent(),
                chatNotificationDto.getTime(),
                chatNotificationDto.getType(),
                checkedChatId
        );
    }
}
