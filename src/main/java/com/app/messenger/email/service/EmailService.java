package com.app.messenger.email.service;

import com.app.messenger.email.dto.EmailDto;
import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {
    void sendHtmlPageEmail(
            String to,
            String subject,
            String templateName,
            Map<String, Object> extraProperties
    ) throws MessagingException;

    void sendEmailForAccountActivation(
            String to,
            String username,
            String activationCode
    ) throws MessagingException;

    EmailDto buildEmail(String to, String subject, String text);
}
