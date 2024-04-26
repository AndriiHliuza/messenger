package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.exception.UserNotAuthenticatedException;
import com.app.messenger.security.service.AuthenticationService;
import com.app.messenger.websocket.controller.dto.ChatDto;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.repository.ChatMemberRepository;
import com.app.messenger.websocket.repository.MessageRepository;
import com.app.messenger.websocket.repository.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatConverter implements Converter<ChatDto, Chat> {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MessageRepository messageRepository;
    private final UserConverter userConverter;
    private final MessageConverter messageConverter;

    @Override
    public ChatDto toDto(Chat chat) throws Exception {
        UUID chatId = chat.getId();
        List<ChatMemberDto> convertedChatMembers = new ArrayList<>();
        List<MessageDto> convertedChatMessages = new ArrayList<>();

        List<ChatMember> chatMembers = chatMemberRepository.findAllByChatId(chatId);
        for (ChatMember chatMember : chatMembers) {
            UserDto userDto = userConverter.toDto(chatMember.getMember());
            MemberRole memberRole = chatMember.getMemberRole();
            String convertedChatId = chatMember.getChat().getId().toString();

            convertedChatMembers.add(
                    ChatMemberDto
                            .builder()
                            .chatId(convertedChatId)
                            .user(userDto)
                            .role(memberRole)
                            .build()
            );
        }

//        List<Message> chatMessages = messageRepository.findAllByChatIdOrderByChatMessageNumber(chatId);
        List<Message> chatMessages = messageRepository.findAllByChatIdOrderBySendTime(chatId);
        for (Message message : chatMessages) {
            convertedChatMessages.add(messageConverter.toDto(message));
        }

        return setChatNameForCurrentUser(ChatDto
                .builder()
                .id(chat.getId().toString())
                .name(chat.getName())
                .type(chat.getType().name())
                .members(convertedChatMembers)
                .messages(convertedChatMessages)
                .build());
    }

    @Override
    public Chat toEntity(ChatDto chatDto) throws Exception {
        User currentUser = authenticationService.getCurrentUser();

        ChatType chatType = ChatType.valueOf(chatDto.getType().toUpperCase());
        Chat chat = Chat
                .builder()
                .name(chatDto.getName())
                .type(chatType)
                .build();

        List<ChatMember> membersToAddToChat = new ArrayList<>();
        Collection<ChatMemberDto> members = chatDto.getMembers();
        for (ChatMemberDto chatMemberDto : members) {
            UserDto userDto = chatMemberDto.getUser();
            String username = userDto.getUsername();
            MemberRole memberRole = chatMemberDto.getRole();
            userRepository
                    .findByUsername(username)
                    .ifPresent(user -> membersToAddToChat.add(
                            ChatMember
                                    .builder()
                                    .chat(chat)
                                    .member(user)
                                    .memberRole(memberRole)
                                    .build()
                    ));
        }
        if (membersToAddToChat.isEmpty()) {
            throw new IllegalArgumentException("Chat has no members to add");
        }
        membersToAddToChat.add(
                ChatMember
                        .builder()
                        .chat(chat)
                        .member(currentUser)
                        .memberRole(MemberRole.ADMIN)
                        .build()
        );

        chat.setMembers(membersToAddToChat);
        return chat;
    }

    private ChatDto setChatNameForCurrentUser(ChatDto chatDto) throws UserNotAuthenticatedException {
        User currentUser = authenticationService.getCurrentUser();
        ChatType chatType = ChatType.valueOf(chatDto.getType().toUpperCase());
        String chatName = switch (chatType) {
            case GROUP_CHAT -> chatDto.getName();
            case PRIVATE_CHAT -> chatDto
                    .getMembers()
                    .stream()
                    .filter(chatMemberDto -> !chatMemberDto
                            .getUser()
                            .getUsername()
                            .equals(currentUser.getUsername()))
                    .findFirst()
                    .map(chatMemberDto -> chatMemberDto.getUser().getUniqueName())
                    .orElseThrow();
        };
        chatDto.setName(chatName);
        return chatDto;
    }
}
