package com.app.messenger.service.converter;

import com.app.messenger.controller.dto.Subscription;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.SubscriptionSubscriberRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.SubscriptionSubscriber;
import com.app.messenger.repository.model.User;
import com.app.messenger.service.converter.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionSubscriberConverter implements Converter<Subscription, SubscriptionSubscriber> {

    private final UserRepository userRepository;
    private final SubscriptionSubscriberRepository subscriptionSubscriberRepository;

    @Override
    public Subscription toDto(SubscriptionSubscriber subscriptionSubscriber) throws Exception {
        String subscriptionUniqueName = null;
        String subscriberUniqueName = null;
        boolean isSubscribed = false;
        User subscription = subscriptionSubscriber.getSubscription();
        if (subscription != null) {
            subscriptionUniqueName = subscription.getUniqueName();
        }
        User subscriber = subscriptionSubscriber.getSubscriber();
        if (subscriber != null) {
            subscriberUniqueName = subscriber.getUniqueName();
        }

        if (subscriptionSubscriberRepository.existsBySubscriptionAndSubscriber(
                subscription,
                subscriber
        ) && subscriptionUniqueName != null && subscriberUniqueName != null) {
            isSubscribed = true;
        }

        return Subscription
                .builder()
                .subscriptionUniqueName(subscriptionUniqueName)
                .subscriberUniqueName(subscriberUniqueName)
                .isSubscribed(isSubscribed)
                .build();
    }

    @Override
    public SubscriptionSubscriber toEntity(Subscription subscription) throws Exception {
        String subscriptionUniqueName = subscription.getSubscriptionUniqueName();
        User subscriptionEntity = userRepository
                .findByRoleAndUniqueName(Role.USER, subscriptionUniqueName)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + subscriptionUniqueName + " not found")
                );

        String subscriberUniqueName = subscription.getSubscriberUniqueName();
        User subscriberEntity = userRepository
                .findByRoleAndUniqueName(Role.USER, subscriberUniqueName)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + subscriberUniqueName + " not found")
                );

        return SubscriptionSubscriber
                .builder()
                .subscription(subscriptionEntity)
                .subscriber(subscriberEntity)
                .build();
    }
}
