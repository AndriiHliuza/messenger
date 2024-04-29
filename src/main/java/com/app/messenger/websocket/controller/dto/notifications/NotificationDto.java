package com.app.messenger.websocket.controller.dto.notifications;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotificationDto.class, name = "notification"),
        @JsonSubTypes.Type(value = ChatNotificationDto.class, name = "chatNotification")
})
public class NotificationDto implements Cloneable {
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private String time;
    private String type;

    public NotificationDto cloneAndSetReceiverUsername(String receiverUsername) throws CloneNotSupportedException {
        NotificationDto clonedNotificationDto = clone();
        clonedNotificationDto.setReceiverUsername(receiverUsername);
        return clonedNotificationDto;
    }

    @Override
    public NotificationDto clone() throws CloneNotSupportedException {
        return (NotificationDto) super.clone();
    }
}
