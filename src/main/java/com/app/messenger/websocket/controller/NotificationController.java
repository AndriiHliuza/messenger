package com.app.messenger.websocket.controller;

import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.websocket.controller.dto.notifications.NotificationDto;
import com.app.messenger.websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/notifications") // endpoint /api/messaging/notifications
    public void processAndSendNotificationToUser(@Payload NotificationDto notificationDto) throws UserNotFoundException {
        // endpoint /api/messaging/topic/notificationDto.getReceiverUsername()/notifications
        notificationService.processAndSendNotificationToUser(notificationDto);
    }
}
