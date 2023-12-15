package com.app.messenger.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions_subscribers")
public class SubscriptionSubscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "subscription_id", referencedColumnName = "id", nullable = false)
    private User subscription;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", referencedColumnName = "id", nullable = false)
    private User subscriber;
}
