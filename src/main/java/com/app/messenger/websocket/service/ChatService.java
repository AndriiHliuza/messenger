package com.app.messenger.websocket.service;

import com.app.messenger.websocket.controller.dto.ChatDto;

import java.util.Collection;

public interface ChatService {
    Collection<ChatDto> getCurrentUserChats(String chatType, Long numberOfMessagesToRetrieveFromEveryChat) throws Exception;
    ChatDto getChatById(String chatId) throws Exception;
    ChatDto getCurrentUserPrivateChatWithAnotherUserByAnotherUserUniqueName(String anotherUserUniqueName) throws Exception;
    ChatDto createChat(ChatDto chatDto) throws Exception;
}
