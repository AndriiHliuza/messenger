package com.app.messenger.websocket.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.service.AuthenticationService;
import com.app.messenger.service.ChatConverter;
import com.app.messenger.service.ChatMemberConverter;
import com.app.messenger.websocket.controller.dto.ChatDto;
import com.app.messenger.websocket.controller.dto.ChatMemberDto;
import com.app.messenger.websocket.controller.dto.MessageDto;
import com.app.messenger.websocket.exception.ChatMemberAlreadyExistsException;
import com.app.messenger.websocket.exception.ChatMemberNotFoundException;
import com.app.messenger.websocket.exception.ChatModificationException;
import com.app.messenger.websocket.exception.ChatNotFoundException;
import com.app.messenger.websocket.repository.ChatMemberRepository;
import com.app.messenger.websocket.repository.ChatRepository;
import com.app.messenger.websocket.repository.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatConverter chatConverter;
    private final ChatMemberConverter chatMemberConverter;

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
    public ChatDto updateChat(String chatId, ChatDto chatDto) throws Exception {
        if (!chatDto.getId().equals(chatId)) {
            throw new IllegalArgumentException("Chat ids are different");
        }

        UUID convertedChatId = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(convertedChatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id " + chatId + " not found")
                );

        User currentUser = authenticationService.getCurrentUser();
        if (!chatMemberRepository.existsByMemberIdAndChatIdAndMemberRole(
                currentUser.getId(),
                chat.getId(),
                MemberRole.ADMIN)
        ) {
            throw new ChatMemberNotFoundException("Chat member with id " + currentUser.getId()
                    + " and role " + MemberRole.ADMIN
                    + " not found in chat with id " + chatId);
        }

        String newChatName = chatDto.getName();
        if (newChatName == null || newChatName.isBlank()) {
            throw new IllegalArgumentException("Chat name is null or blank");
        }

        chat.setName(newChatName);
        Chat savedChat = chatRepository.save(chat);
        ChatDto chatDtoToReturn = chatConverter.toDto(savedChat);
        messageService.sendMessageToChatOnChatUpdate(chatDtoToReturn);

        return chatDtoToReturn;
    }

    @Override
    public ChatDto deleteChat(String chatId) throws Exception {
        UUID convertedChatId = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(convertedChatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id " + chatId + " not found")
                );

        User currentUser = authenticationService.getCurrentUser();

        if (!chatMemberRepository.existsByMemberIdAndChatIdAndMemberRole(
                currentUser.getId(),
                chat.getId(),
                MemberRole.ADMIN)
        ) {
            throw new ChatMemberNotFoundException("Chat member with member id " + currentUser.getId()
                    + " and member role " + MemberRole.ADMIN
                    + " not found in chat with id  " + chatId);
        }

        ChatDto chatDtoToReturn = chatConverter.toDto(chat);
        chatRepository.deleteById(chat.getId());
        notificationService.notifyUsersOnChatDeletion(chatDtoToReturn);
        return chatDtoToReturn;
    }

    @Override
    public ChatDto getCurrentUserPrivateChatWithAnotherUserByAnotherUserUsername(String anotherUserUsername) throws Exception {
        ChatDto chatDtoToReturn = null;

        User currentUser = authenticationService.getCurrentUser();
        String currentUserUsername = currentUser.getUsername();

        Chat chat = chatRepository.findPrivateChatByPrivateChatMembersUsernames(currentUserUsername, anotherUserUsername);
        if (chat != null) {
            chatDtoToReturn = chatConverter.toDto(chat);
            Collection<MessageDto> encryptedMessages = messageService
                    .encryptAllMessagesUsingE2EEWithCurrentUserPublicKey(chatDtoToReturn.getMessages());
            chatDtoToReturn.setMessages(encryptedMessages);
        }

        return chatDtoToReturn;
    }

    @Override
    public ChatDto createChat(ChatDto chatDto) throws Exception {
        Chat chat = chatConverter.toEntity(chatDto);
        Chat savedChat = chatRepository.save(chat);
        ChatDto chatDtoToReturn = chatConverter.toDto(savedChat);
        notificationService.notifyUsersOnChatCreation(chatDtoToReturn);
        return chatDtoToReturn;
    }

    @Override
    public ChatMemberDto addUserToChat(String chatId, String username, UserDto user) throws Exception {
        if (!username.equals(user.getUsername())) {
            throw new IllegalArgumentException("Usernames are different");
        }

        UUID convertedChatId = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(convertedChatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id " + chatId + " not found")
                );

        if (chat.getType().equals(ChatType.PRIVATE_CHAT)) {
            throw new IllegalArgumentException("User can not be added to private chat");
        }

        User userToAdd = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + username + " not found")
                );

        if (chatMemberRepository.existsByMemberIdAndChatId(userToAdd.getId(), chat.getId())) {
            throw new ChatMemberAlreadyExistsException("Chat member with username "
                    + userToAdd.getUsername() + " already exists in chat with id " + chatId);
        }

        ChatMember chatMember = ChatMember
                .builder()
                .chat(chat)
                .member(userToAdd)
                .memberRole(MemberRole.MEMBER)
                .build();

        ChatMember savedChatMember = chatMemberRepository.save(chatMember);
        messageService.createMessagesStatusesForUserInChat(
                username,
                UserType.RECEIVER,
                chat.getId(),
                Status.READ_MESSAGE
        );

        ChatMemberDto chatMemberToReturn = chatMemberConverter.toDto(savedChatMember);

        messageService.sendMessageToChatOnActionOnUserInChat(chatMemberToReturn, MessageType.CHAT_MEMBER_ADDED_TO_CHAT);
        notificationService.notifyChatMemberOnActionOnChatMemberInChat(
                chatConverter.toDto(chat),
                chatMemberToReturn,
                NotificationType.USER_ADDED_TO_CHAT_NOTIFICATION
        );

        return chatMemberToReturn;
    }

    @Override
    public ChatMemberDto updateChatMember(String chatId, String username, ChatMemberDto chatMemberDto) throws Exception {
        UserDto userDto = chatMemberDto.getUser();
        if (!userDto.getUsername().equals(username)) {
            throw new IllegalArgumentException("Usernames are different");
        }

        if (!chatMemberDto.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Chat ids are different");
        }

        UUID convertedChatId = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(convertedChatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id " + chatId + " not found")
                );

        ChatMember chatMember = chatMemberRepository
                .findByChatIdAndMemberUsername(chat.getId(), username)
                .orElseThrow(
                        () -> new ChatMemberNotFoundException("Chat member with username " + username
                                + " not found in chat with id " + chatId)
                );

        if (chatMemberDto.getRole() == null) {
            throw new IllegalArgumentException("Chat member role is null");
        }

        chatMember.setMemberRole(chatMemberDto.getRole());
        ChatMember savedChatMember = chatMemberRepository.save(chatMember);

        ChatMemberDto chatMemberDtoToReturn = chatMemberConverter.toDto(savedChatMember);

        messageService.sendMessageToChatOnActionOnUserInChat(chatMemberDtoToReturn, MessageType.NEW_STATUS_IN_CHAT_MEMBER);
        notificationService.notifyChatMemberOnActionOnChatMemberInChat(
                chatConverter.toDto(chat),
                chatMemberDtoToReturn,
                NotificationType.NEW_ADMIN_IN_CHAT_NOTIFICATION
        );

        return chatMemberDtoToReturn;
    }

    @Override
    public ChatMemberDto deleteChatMember(String chatId, String username) throws Exception {
        UUID convertedChatId = UUID.fromString(chatId);
        Chat chat = chatRepository
                .findById(convertedChatId)
                .orElseThrow(
                        () -> new ChatNotFoundException("Chat with id " + chatId + " not found")
                );

        if (chat.getType().equals(ChatType.PRIVATE_CHAT)) {
            throw new ChatModificationException("Users can not be deleted from private chats");
        }

        ChatMember chatMemberToDelete = chatMemberRepository
                .findByChatIdAndMemberUsername(chat.getId(), username)
                .orElseThrow(
                        () -> new ChatMemberNotFoundException("Chat member with username " + username
                                + " not found in chat with id " + chatId)
                );
        User userToDeleteFromChat = chatMemberToDelete.getMember();
        User currentUser = authenticationService.getCurrentUser();

        if (!currentUser.getId().equals(userToDeleteFromChat.getId())
                && !chatMemberRepository.existsByMemberIdAndChatIdAndMemberRole(
                currentUser.getId(),
                chat.getId(),
                MemberRole.ADMIN)
        ) {
            throw new ChatMemberNotFoundException("User with username " + currentUser.getUsername()
                    + " can not delete user with username " + userToDeleteFromChat.getUsername()
                    + " from chat with id " + chatId);
        }

        chatMemberRepository.delete(chatMemberToDelete);

        ChatMemberDto chatMemberDtoToReturn = chatMemberConverter.toDto(chatMemberToDelete);

        NotificationType notificationType = NotificationType.DELETED_CHAT_MEMBER_NOTIFICATION;
        MessageType messageType = MessageType.CHAT_MEMBER_DELETED_FROM_CHAT;

        if (currentUser.getUsername().equals(chatMemberDtoToReturn.getUser().getUsername())) {
            notificationType = NotificationType.MEMBER_LEFT_CHAT_NOTIFICATION;
            messageType = MessageType.CHAT_MEMBER_LEFT_CHAT;
        }

        List<ChatMember> chatMembers = chatMemberRepository.findAllByChatId(chat.getId());

        if (chatMembers.isEmpty()) {
            notificationType = NotificationType.DELETED_GROUP_CHAT_NOTIFICATION;
            chatRepository.deleteById(convertedChatId);
        }

        notificationService.notifyChatMemberOnActionOnChatMemberInChat(
                chatConverter.toDto(chat),
                chatMemberDtoToReturn,
                notificationType
        );

        if (!notificationType.equals(NotificationType.DELETED_GROUP_CHAT_NOTIFICATION)) {
            messageService.sendMessageToChatOnActionOnUserInChat(chatMemberDtoToReturn, messageType);
        }

        return chatMemberDtoToReturn;
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
