package com.app.messenger.email.service;

import com.app.messenger.email.dto.EmailDto;
import com.app.messenger.email.dto.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("${application.email.from}")
    private String FROM;

    @Value("${application.cors.origins.url}")
    private String CORS_ALLOWED_ORIGINS;

    @Override
    @Async
    public void sendHtmlPageEmail(
            String to,
            String subject,
            String templateName,
            Map<String, Object> extraProperties
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Context context = new Context();
        context.setVariables(extraProperties);

        mimeMessageHelper.setFrom(FROM);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        String template = springTemplateEngine.process(templateName, context);
        mimeMessageHelper.setText(template, true);
        javaMailSender.send(mimeMessage);
    }

    @Override
    @Async
    public void sendEmailForAccountActivation(
            String to,
            String username,
            String activationCode
    ) throws MessagingException {
        Map<String, Object> extraProperties = new HashMap<>();
        extraProperties.put("username", username);
        extraProperties.put("confirmationUrl", CORS_ALLOWED_ORIGINS + "/user/" + username + "/account/activation");
        extraProperties.put("activationCode", activationCode);

        sendHtmlPageEmail(
                to,
                EmailTemplate.ACCOUNT_ACTIVATION.getDescription(),
                EmailTemplate.ACCOUNT_ACTIVATION.getName(),
                extraProperties
        );
    }

    @Override
    public EmailDto buildEmail(String to, String subject, String text) {
        return EmailDto
                .builder()
                .from(FROM)
                .to(to)
                .subject(subject)
                .text(subject)
                .build();
    }
}
