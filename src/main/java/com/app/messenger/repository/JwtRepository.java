package com.app.messenger.repository;

import com.app.messenger.repository.model.Jwt;
import com.app.messenger.repository.model.TokenTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, UUID> {
    boolean existsByContent(String content);
    List<Jwt> findByUserId(UUID id);
    Optional<Jwt> findByUserIdAndTargetType(UUID userId, TokenTargetType targetType);
}
