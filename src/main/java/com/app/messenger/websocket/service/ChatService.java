package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.websocket.controller.dto.ChatDto;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;

import java.util.Collection;

public interface ChatService {
    Collection<ChatDto> getCurrentUserChats(String chatType, Long numberOfMessagesToRetrieveFromEveryChat) throws Exception;
    ChatDto getChatById(String chatId) throws Exception;
    ChatDto updateChat(String chatId, ChatDto chatDto) throws Exception;
    ChatDto deleteChat(String chatId) throws Exception;
    ChatDto getCurrentUserPrivateChatWithAnotherUserByAnotherUserUsername(String anotherUserUsername) throws Exception;
    ChatDto createChat(ChatDto chatDto) throws Exception;
    ChatMemberDto addUserToChat(String chatId, String username, UserDto user) throws Exception;
    ChatMemberDto updateChatMember(String chatId, String username, ChatMemberDto chatMemberDto) throws Exception;
    ChatMemberDto deleteChatMember(String chatId, String username) throws Exception;
}
