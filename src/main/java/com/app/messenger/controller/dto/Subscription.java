package com.app.messenger.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private String subscriptionUniqueName;
    private String subscriberUniqueName;
    private boolean isSubscribed;
}
