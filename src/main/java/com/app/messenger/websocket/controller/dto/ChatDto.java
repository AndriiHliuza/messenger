package com.app.messenger.websocket.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private String id;
    private String name;
    private String type;
    private Collection<ChatMemberDto> members;
    private Collection<MessageDto> messages;
}
