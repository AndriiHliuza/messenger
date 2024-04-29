package com.app.messenger.websocket.controller.dto.notifications;

import com.app.messenger.controller.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiUserNotificationDto {
    private NotificationDto notification;
    private List<UserDto> usersToNotify;
}
