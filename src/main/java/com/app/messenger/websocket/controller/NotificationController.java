package com.app.messenger.websocket.controller;

import com.app.messenger.websocket.controller.dto.notifications.MultiUserNotificationDto;
import com.app.messenger.websocket.controller.dto.notifications.NotificationDto;
import com.app.messenger.websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/notification") // endpoint /api/messaging/notification
    public void processAndSendNotificationToUser(@Payload NotificationDto notificationDto) {
        // endpoint /api/messaging/topic/notificationDto.getReceiverUsername()/notifications
        notificationService.processAndSendNotificationToUser(notificationDto);
    }

    @MessageMapping("/notifications") // endpoint /api/messaging/notifications
    public void processAndSendNotificationToUsers(@Payload MultiUserNotificationDto multiUserNotificationDto) {
        // endpoint /api/messaging/topic/notificationDto.getReceiverUsername()/notifications
        notificationService.processAndSendMultiUserNotification(multiUserNotificationDto);
    }
}
