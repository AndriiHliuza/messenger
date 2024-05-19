package com.app.messenger.service.converter;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;
import com.app.messenger.websocket.exception.ChatNotFoundException;
import com.app.messenger.websocket.repository.ChatRepository;
import com.app.messenger.websocket.repository.model.Chat;
import com.app.messenger.websocket.repository.model.ChatMember;
import com.app.messenger.websocket.repository.model.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMemberConverter implements Converter<ChatMemberDto, ChatMember> {
    private final UserConverter userConverter;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    @Override
    public ChatMemberDto toDto(ChatMember chatMember) throws Exception {
        UUID chatId = chatMember.getChat().getId();
        UserDto userDto = userConverter.toDto(chatMember.getMember());
        MemberRole memberRole = chatMember.getMemberRole();
        return ChatMemberDto
                .builder()
                .chatId(chatId.toString())
                .user(userDto)
                .role(memberRole)
                .build();
    }

    @Override
    public ChatMember toEntity(ChatMemberDto chatMemberDto) throws Exception {
        String chatId = chatMemberDto.getChatId();
        UUID convertedChatId = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(convertedChatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id " + chatId + " not found")
                );

        UserDto userDto = chatMemberDto.getUser();
        if (userDto == null) {
            throw new IllegalArgumentException("User is null");
        }
        String username = userDto.getUsername();
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + username + " not found")
                );

        MemberRole memberRole = chatMemberDto.getRole();
        if (memberRole == null) {
            throw new IllegalArgumentException("Member role is null");
        }
        return ChatMember
                .builder()
                .chat(chat)
                .member(user)
                .memberRole(memberRole)
                .build();
    }
}
