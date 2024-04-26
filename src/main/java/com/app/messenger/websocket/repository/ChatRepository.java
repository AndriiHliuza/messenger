package com.app.messenger.websocket.repository;

import com.app.messenger.websocket.repository.model.Chat;
import com.app.messenger.websocket.repository.model.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("""
            SELECT c FROM Chat c
            JOIN ChatMember cm ON c.id = cm.chat.id
            JOIN User u ON cm.member.id = u.id
            WHERE u.username = :username
            """)
    List<Chat> findAllChatsByChatMemberUsername(String username);

    @Query("""
            SELECT c FROM Chat c
            JOIN ChatMember cm ON c.id = cm.chat.id
            JOIN User u ON cm.member.id = u.id
            WHERE u.username = :username AND c.type = :type
            """)
    List<Chat> findAllChatsByChatMemberUsernameAndChatType(String username, ChatType type);

    @Query("""
            SELECT c FROM Chat c
            JOIN ChatMember cm1 ON c.id = cm1.chat.id
            JOIN User u1 ON cm1.member.id = u1.id AND u1.username = :firstMemberUsername
            JOIN ChatMember cm2 ON c.id = cm2.chat.id
            JOIN User u2 ON cm2.member.id = u2.id AND u2.username = :secondMemberUsername
            WHERE c.type = 'PRIVATE_CHAT'
            """)
    Chat findPrivateChatByPrivateChatMembersUsernames(String firstMemberUsername, String secondMemberUsername);

    @Query("""
            SELECT c FROM Chat c 
            JOIN ChatMember cm ON c.id = cm.chat.id 
            WHERE c.id = :chatId AND cm.member.id = :chatMemberId
            """)
    Optional<Chat> findByChatIdAndChatMemberId(UUID chatId, UUID chatMemberId);
}
