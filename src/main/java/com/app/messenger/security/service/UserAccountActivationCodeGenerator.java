package com.app.messenger.security.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UserAccountActivationCodeGenerator implements CodeGenerator {
    @Override
    public String generate(int length) {
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(10);
            codeBuilder.append(randomIndex);
        }

        return codeBuilder.toString();
    }
}
