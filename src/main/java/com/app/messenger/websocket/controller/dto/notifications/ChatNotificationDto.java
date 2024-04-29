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

    public ChatNotificationDto(
            String senderUsername,
            String receiverUsername,
            String content,
            String time,
            String type,
            String chatId,
            String chatName,
            String chatType
    ) {
        super(senderUsername, receiverUsername, content, time, type);
        this.chatId = chatId;
        this.chatName = chatName;
        this.chatType = chatType;
    }

    public ChatNotificationDto cloneAndSetReceiverUsernameAndChatName(String receiverUsername, String chatName) throws CloneNotSupportedException {
        ChatNotificationDto chatNotificationDto = (ChatNotificationDto) super.cloneAndSetReceiverUsername(receiverUsername);
        chatNotificationDto.setChatName(chatName);
        return chatNotificationDto;
    }

    @Override
    public ChatNotificationDto clone() throws CloneNotSupportedException {
        return (ChatNotificationDto) super.clone();
    }
}
