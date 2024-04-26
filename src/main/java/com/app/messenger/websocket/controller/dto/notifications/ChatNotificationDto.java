package com.app.messenger.websocket.controller.dto.notifications;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatNotificationDto extends NotificationDto {
    private String chatId;
    private String chatName;
    private String chatType;

    public ChatNotificationDto(String receiverUsername, String content, String time, String type, String chatId, String chatName, String chatType) {
        super(receiverUsername, content, time, type);
        this.chatId = chatId;
        this.chatName = chatName;
        this.chatType = chatType;
    }
}
