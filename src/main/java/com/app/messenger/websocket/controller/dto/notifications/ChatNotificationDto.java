package com.app.messenger.websocket.controller.dto.notifications;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatNotificationDto extends NotificationDto {
    private String chatId;

    public ChatNotificationDto(String receiverUsername, String content, String time, String type, String chatId) {
        super(receiverUsername, content, time, type);
        this.chatId = chatId;
    }
}
