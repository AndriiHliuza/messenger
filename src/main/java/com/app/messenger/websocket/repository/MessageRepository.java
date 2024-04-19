package com.app.messenger.websocket.repository;

import com.app.messenger.websocket.repository.model.Chat;
import com.app.messenger.websocket.repository.model.Message;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByChat(Chat chat);
    List<Message> findAllByChatIdOrderBySendTime(UUID chatId);
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    Message findTopByChatIdOrderByChatMessageNumberDesc(UUID chatId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void deleteById(@NonNull UUID id);
}
