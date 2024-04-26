package com.app.messenger.websocket.controller.dto;

import com.app.messenger.websocket.repository.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagesStatusUpdateDto {
    private String chatId;
    private Status status;
}
