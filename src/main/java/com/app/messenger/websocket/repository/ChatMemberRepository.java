package com.app.messenger.websocket.repository;

import com.app.messenger.repository.model.User;
import com.app.messenger.websocket.repository.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, UUID> {
    List<ChatMember> findAllByChatId(UUID chatId);
}
