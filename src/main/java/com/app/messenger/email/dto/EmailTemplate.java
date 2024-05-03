package com.app.messenger.email.dto;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    ACCOUNT_ACTIVATION("account_activation");

    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }
}
