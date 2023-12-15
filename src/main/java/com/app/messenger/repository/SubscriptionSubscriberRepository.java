package com.app.messenger.repository;

import com.app.messenger.repository.model.SubscriptionSubscribers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionSubscribersRepository extends JpaRepository<SubscriptionSubscribers, UUID> {
}
