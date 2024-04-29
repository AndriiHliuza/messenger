package com.app.messenger.websocket.repository;

import com.app.messenger.websocket.repository.model.MessageStatus;
import com.app.messenger.websocket.repository.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, UUID> {
    Optional<MessageStatus> findByMessageIdAndUserId(UUID messageId, UUID userId);

    @Query("""
            SELECT ms FROM MessageStatus ms
            JOIN Message m ON m.id = ms.message.id
            where m.chat.id = :chatId AND ms.user.id = :userId AND ms.status = :status
            """)
    List<MessageStatus> findAllByChatIdAndUserIdAndStatus(UUID chatId, UUID userId, Status status);

    @Query("""
            SELECT ms FROM MessageStatus ms
            JOIN Message m ON m.id = ms.message.id
            WHERE m.chat.id = :chatId AND ms.status = :status
            """)
    List<MessageStatus> findAllByChatIdAndStatus(UUID chatId, Status status);
}
