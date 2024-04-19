package com.app.messenger.websocket.controller.dto;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.websocket.repository.model.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberDto {
    private String chatId;
    private UserDto user;
    private MemberRole role;
}
