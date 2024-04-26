package com.app.messenger.websocket.controller;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.websocket.controller.dto.ChatDto;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/api/chats")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ChatDto> getCurrentUserChats(
            @RequestParam(required = false) String chatType,
            @RequestParam(required = false) Long numberOfMessagesToRetrieveFromEveryChat
    ) throws Exception {
        return chatService.getCurrentUserChats(chatType, numberOfMessagesToRetrieveFromEveryChat);
    }

    @PostMapping("/api/chats")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ChatDto createChat(@RequestBody ChatDto chatDto) throws Exception {
        return chatService.createChat(chatDto);
    }

    @GetMapping("/api/chats/{chatId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ChatDto getChatById(@PathVariable String chatId) throws Exception {
        return chatService.getChatById(chatId);
    }

    @PatchMapping("/api/chats/{chatId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ChatDto updateChat(
            @PathVariable String chatId,
            @RequestBody ChatDto chatDto
    ) throws Exception {
        return chatService.updateChat(chatId, chatDto);
    }

    @DeleteMapping("/api/chats/{chatId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ChatDto deleteChat(@PathVariable String chatId) throws Exception {
        return chatService.deleteChat(chatId);
    }

    @PostMapping("/api/chats/{chatId}/members/{username}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ChatMemberDto addUserToChat(
            @PathVariable String chatId,
            @PathVariable String username,
            @RequestBody UserDto user
    ) throws Exception {
        return chatService.addUserToChat(chatId, username, user);
    }

    @PatchMapping("/api/chats/{chatId}/members/{username}")
    @PreAuthorize("hasRole('USER')")
    public ChatMemberDto updateChatMember(
            @PathVariable String chatId,
            @PathVariable String username,
            @RequestBody ChatMemberDto chatMemberDto
    ) throws Exception {
        return chatService.updateChatMember(chatId, username, chatMemberDto);
    }

    @DeleteMapping("/api/chats/{chatId}/members/{username}")
    @PreAuthorize("hasRole('USER')")
    public ChatMemberDto deleteChatMember(
            @PathVariable String chatId,
            @PathVariable String username
    ) throws Exception {
        return chatService.deleteChatMember(chatId, username);
    }

    @GetMapping("/api/private-chats/{anotherUserUsername}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ChatDto getCurrentUserPrivateChatWithAnotherUserByAnotherUserUsername(
            @PathVariable String anotherUserUsername
    ) throws Exception {
        return chatService.getCurrentUserPrivateChatWithAnotherUserByAnotherUserUsername(anotherUserUsername);
    }

    @MessageMapping("/official-channel") // endpoint /api/messaging/official-channel
    @SendTo("/api/messaging/topic/official-channel") //endpoint /api/messaging/topic/official-channel
    public MessageDto sendMessageToTheApplicationChat(@Payload MessageDto messageDto) {
        System.out.println("------------------------------------------------");
        return MessageDto
                .builder()
                .sender(messageDto.getSender())
                .chatId(messageDto.getChatId())
                .content("Modified message: " + messageDto.getContent())
                .sendTime(messageDto.getSendTime())
                .type(messageDto.getType())
                .status(messageDto.getStatus())
                .build();
    }

    // todo realise /chats id. not /messages
    @MessageMapping("/chats") // endpoint /api/messaging/chats
    public void processChatMessage(@Payload MessageDto messageDto) {
        // endpoint /api/messaging/topic/chats/messageDto.getChatId()/messages
        String chatId = messageDto.getChatId();
        simpMessagingTemplate.convertAndSend(
                "/api/messaging/topic/chats/" + chatId + "/messages",
                MessageDto
                        .builder()
                        .sender(messageDto.getSender())
                        .chatId(messageDto.getChatId())
                        .content("Private message: " + messageDto.getContent())
                        .sendTime(messageDto.getSendTime())
                        .type(messageDto.getType())
                        .build());
    }
}
