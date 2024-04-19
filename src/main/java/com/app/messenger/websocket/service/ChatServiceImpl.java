package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.service.AuthenticationService;
import com.app.messenger.service.ChatConverter;
import com.app.messenger.websocket.controller.dto.ChatDto;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.controller.dto.notifications.ChatNotificationDto;
import com.app.messenger.websocket.exception.ChatNotFoundException;
import com.app.messenger.websocket.repository.ChatRepository;
import com.app.messenger.websocket.repository.model.Chat;
import com.app.messenger.websocket.repository.model.ChatType;
import com.app.messenger.websocket.repository.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final ChatRepository chatRepository;
    private final ChatConverter chatConverter;

    @Override
    public Collection<ChatDto> getCurrentUserChats(String chatType, Long numberOfMessagesToRetrieveFromEveryChat) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        String currentUserUsername = currentUser.getUsername();
        ChatType type = null;
        List<Chat> chats;

        if (chatType != null) {
            type = ChatType.valueOf(chatType.toUpperCase());
        }

        if (type != null) {
            chats = chatRepository.findAllChatsByChatMemberUsernameAndChatType(currentUserUsername, type);
        } else {
            chats = chatRepository.findAllChatsByChatMemberUsername(currentUserUsername);
        }

        List<ChatDto> chatsToReturn = new ArrayList<>();
        for (Chat chat : chats) {
            chatsToReturn.add(chatConverter.toDto(chat));
        }

        if (numberOfMessagesToRetrieveFromEveryChat != null) {
            chatsToReturn = modifyNumberOfMessagesInEveryChat(chatsToReturn, numberOfMessagesToRetrieveFromEveryChat);
        }

        for (ChatDto chatDto : chatsToReturn) {
            Collection<MessageDto> encryptedMessages = messageService
                    .encryptAllMessagesUsingE2EEWithCurrentUserPublicKey(chatDto.getMessages());
            chatDto.setMessages(encryptedMessages);
        }

        return chatsToReturn;
    }

    @Override
    public ChatDto getChatById(String chatId) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        UUID userId = currentUser.getId();
        String username = currentUser.getUsername();
        UUID chatUUID = UUID.fromString(chatId);

        Chat chat = chatRepository
                .findByChatIdAndChatMemberId(chatUUID, userId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with name: " + chatId + "and user: " + username + " not found in database")
                );
        ChatDto chatDtoToReturn = chatConverter.toDto(chat);
        Collection<MessageDto> encryptedMessages = messageService
                .encryptAllMessagesUsingE2EEWithCurrentUserPublicKey(chatDtoToReturn.getMessages());
        chatDtoToReturn.setMessages(encryptedMessages);
        return chatDtoToReturn;
    }

    @Override
    public ChatDto getCurrentUserPrivateChatWithAnotherUserByAnotherUserUniqueName(String anotherUserUniqueName) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        String currentUserUniqueName = currentUser.getUniqueName();

        Chat chat = chatRepository.findPrivateChatByPrivateChatMembersUniqueNames(currentUserUniqueName, anotherUserUniqueName);
        ChatDto chatDtoToReturn = chatConverter.toDto(chat);
        Collection<MessageDto> encryptedMessages = messageService
                .encryptAllMessagesUsingE2EEWithCurrentUserPublicKey(chatDtoToReturn.getMessages());
        chatDtoToReturn.setMessages(encryptedMessages);
        return chatDtoToReturn;
    }

    @Override
    public ChatDto createChat(ChatDto chatDto) throws Exception {
        Chat chat = chatConverter.toEntity(chatDto);
        Chat savedChat = chatRepository.save(chat);
        ChatDto chatDtoToReturn = chatConverter.toDto(savedChat);

        List<UserDto> membersToNotify = chatDtoToReturn
                .getMembers()
                .stream()
                .map(ChatMemberDto::getUser)
                .toList();

        notificationService.processAndSendNotificationToUsers(
                new ChatNotificationDto(
                        null,
                        String.format("You have been added to a chat '%s'", chatDtoToReturn.getName()),
                        ZonedDateTime.now().toString(),
                        NotificationType.NEW_CHAT_NOTIFICATION.name(),
                        chatDtoToReturn.getId()
                ),
                membersToNotify
        );
        return chatDtoToReturn;
    }

    private List<ChatDto> modifyNumberOfMessagesInEveryChat(Collection<ChatDto> chats, long numberOfMessagesToLeave) {
        return chats
                .stream()
                .peek(chatDto -> {
                    Collection<MessageDto> messagesToReturn = chatDto.getMessages();
                    if (messagesToReturn.size() >= numberOfMessagesToLeave) {
                        messagesToReturn = messagesToReturn
                                .stream()
                                .toList()
                                .subList(
                                        (int) (messagesToReturn.size() - numberOfMessagesToLeave),
                                        messagesToReturn.size()
                                );
                    }
                    chatDto.setMessages(messagesToReturn);
                }).toList();
    }
}
