package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.websocket.controller.dto.notifications.NotificationDto;

import java.util.Collection;

public interface NotificationService {
    void processAndSendNotificationToUser(NotificationDto notificationDto) throws UserNotFoundException;
    void processAndSendNotificationToUsers(NotificationDto notificationDto, Collection<UserDto> usersToNotify) throws UserNotFoundException;
}
