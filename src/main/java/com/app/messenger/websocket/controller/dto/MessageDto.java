package com.app.messenger.websocket.controller.dto;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.websocket.repository.model.MessageType;
import com.app.messenger.websocket.repository.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String id;
    private UserDto sender;
    private String chatId;
    private String content;
    private String sendTime;
    private MessageType type;
    private Status status;
}
