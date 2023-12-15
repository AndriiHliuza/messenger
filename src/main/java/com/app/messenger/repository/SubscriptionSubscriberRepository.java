package com.app.messenger.repository;

import com.app.messenger.repository.model.SubscriptionSubscriber;
import com.app.messenger.repository.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionSubscriberRepository extends JpaRepository<SubscriptionSubscriber, UUID> {
    boolean existsBySubscriptionAndSubscriber(User subscription, User subscriber);
    SubscriptionSubscriber findBySubscriptionAndSubscriber(User subscription, User subscriber);
}
