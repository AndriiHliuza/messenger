package com.app.messenger.repository;

import com.app.messenger.repository.model.UserAccountActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAccountActivationCodeRepository extends JpaRepository<UserAccountActivationCode, UUID> {
}
