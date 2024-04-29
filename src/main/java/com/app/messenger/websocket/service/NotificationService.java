package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.websocket.controller.dto.ChatDto;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;
import com.app.messenger.websocket.controller.dto.notifications.MultiUserNotificationDto;
import com.app.messenger.websocket.controller.dto.notifications.NotificationDto;
import com.app.messenger.websocket.repository.model.NotificationType;

import java.util.Collection;

public interface NotificationService {
    void processAndSendNotificationToUser(NotificationDto notificationDto);
    void processAndSendNotificationToUsers(NotificationDto notificationDto, Collection<UserDto> usersToNotify);
    void processAndSendMultiUserNotification(MultiUserNotificationDto multiUserNotificationDto);
    void notifyUsersOnChatDeletion(ChatDto deletedChat) throws Exception;
    void notifyUsersOnChatCreation(ChatDto createdChat) throws Exception;
    void notifyChatMemberOnActionOnChatMemberInChat(
            ChatDto chatDto,
            ChatMemberDto chatMemberDto,
            NotificationType notificationType
    ) throws Exception;
}
