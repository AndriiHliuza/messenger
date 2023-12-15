package com.app.messenger.repository;

import com.app.messenger.repository.model.User;
import com.app.messenger.repository.model.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, UUID> {
    UserImage findByUser(User user);
}
