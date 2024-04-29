package com.app.messenger.websocket.controller;

import com.app.messenger.websocket.controller.dto.ChatMessagesStatusUpdateDto;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ResponseStatus(HttpStatus.OK)
public class MessageController {

    private final MessageService messageService;
    @GetMapping("/chats/{chatId}/messages")
    @PreAuthorize("hasRole('USER')")
    public Collection<MessageDto> getAllMessagesFromChat(@PathVariable String chatId) throws Exception {
        return messageService.getAllMessagesFromChat(chatId);
    }

    @PostMapping("/chats/{chatId}/messages")
    @PreAuthorize("hasRole('USER')")
    public MessageDto sendMessageToChat(@RequestBody MessageDto messageDto) throws Exception {
        return messageService.sendMessageToChat(messageDto);
    }

    @PatchMapping("/chats/{chatId}/messages")
    @PreAuthorize("hasRole('USER')")
    public ChatMessagesStatusUpdateDto updateMessagesStatusesInChat(
            @PathVariable String chatId,
            @RequestBody ChatMessagesStatusUpdateDto chatMessagesStatusUpdateDto
            ) throws Exception {
        return messageService.updateMessagesStatusesInChat(chatId, chatMessagesStatusUpdateDto);
    }

    @GetMapping("/chats/{chatId}/messages/{messageId}/status")
    @PreAuthorize("hasRole('USER')")
    public MessageDto getMessageStatusForUser(
            @PathVariable String chatId,
            @PathVariable String messageId,
            @RequestParam String username
    ) throws Exception {
        return messageService.getMessageStatusForUser(chatId, messageId, username);
    }

    @PatchMapping("/chats/{chatId}/messages/{messageId}")
    @PreAuthorize("hasRole('USER')")
    public MessageDto updateMessageInChat(
            @PathVariable String chatId,
            @PathVariable String messageId,
            @RequestBody MessageDto message
    ) throws Exception {
        return messageService.updateMessageInChat(chatId, messageId, message);
    }

    @DeleteMapping("/chats/{chatId}/messages/{messageId}")
    @PreAuthorize("hasRole('USER')")
    public MessageDto deleteMessageFromChat(
            @PathVariable String chatId,
            @PathVariable String messageId
    ) throws Exception {
        return messageService.deleteMessageFromChat(chatId, messageId);
    }
}
