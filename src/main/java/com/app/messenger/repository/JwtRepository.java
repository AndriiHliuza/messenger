package com.app.messenger.repository;

import com.app.messenger.repository.model.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JwtRepository extends JpaRepository<Jwt, UUID> {
    boolean existsByContent(String content);
    Optional<Jwt> findByContent(String content);
    Optional<Jwt> findByUserId(UUID id);
}
