package com.app.messenger.websocket.repository;

import com.app.messenger.websocket.repository.model.ChatMember;
import com.app.messenger.websocket.repository.model.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, UUID> {
    List<ChatMember> findAllByChatId(UUID chatId);
    Optional<ChatMember> findByChatIdAndMemberUsername(UUID chatId, String username);
    boolean existsByMemberId(UUID memberId);
    boolean existsByMemberIdAndChatIdAndMemberRole(UUID memberId, UUID chatId, MemberRole memberRole);
}
