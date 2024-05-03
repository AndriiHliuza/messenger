package com.app.messenger.email.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {
    void sendHtmlPageEmail(
            String from,
            String to,
            String subject,
            String templateName,
            Map<String, Object> extraProperties
    ) throws MessagingException;

    void sendEmailForAccountActivation(
            String from,
            String to,
            String subject,
            String username,
            String confirmationUrl,
            String activationCode
    ) throws MessagingException;
}
