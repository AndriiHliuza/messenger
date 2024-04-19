package com.app.messenger.websocket.controller;

import com.app.messenger.security.exception.UserNotAuthenticatedException;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.exception.ChatNotFoundException;
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

    @DeleteMapping("/chats/{chatId}/messages/{messageId}")
    @PreAuthorize("hasRole('USER')")
    public MessageDto deleteMessageInChat(
            @PathVariable String chatId,
            @PathVariable String messageId
    ) throws Exception {
        return messageService.deleteMessageInChat(chatId, messageId);
    }
}
