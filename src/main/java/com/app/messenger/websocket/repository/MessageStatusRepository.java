package com.app.messenger.websocket.repository;

import com.app.messenger.websocket.repository.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, UUID> {
    Optional<MessageStatus> findByMessageIdAndUserId(UUID messageId, UUID userId);
}
