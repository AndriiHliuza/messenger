package com.app.messenger.email.dto;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    ACCOUNT_ACTIVATION("account_activation", "Account Activation");

    private final String name;
    private final String description;

    EmailTemplate(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
