package com.app.messenger.websocket.repository;

import com.app.messenger.websocket.repository.model.Chat;
import com.app.messenger.websocket.repository.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    boolean existsByIdAndChatId(UUID id, UUID chatId);
    Optional<Message> findByIdAndChatId(UUID id, UUID chatId);
    Optional<Message> findByIdAndSenderIdAndChatId(UUID id, UUID senderId, UUID chatId);
    List<Message> findAllByChat(Chat chat);
    @Query("""
            SELECT m FROM Message m
            WHERE m.chat.id = :chatId
            AND m.id NOT IN (SELECT m.id FROM Message m
            JOIN MessageStatus ms ON m.id = ms.message.id
            WHERE m.chat.id = :chatId AND ms.user.id = :userId)
            """)
    List<Message> findAllMessagesByChatIdThatWereNotSentToUserWithProvidedId(UUID chatId, UUID userId);
    List<Message> findAllByChatIdOrderBySendTime(UUID chatId);;
}
