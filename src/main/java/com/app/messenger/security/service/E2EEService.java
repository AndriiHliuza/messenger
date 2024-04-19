package com.app.messenger.security.service;

import com.app.messenger.security.controller.dto.E2EEDto;

public interface E2EEService {
    E2EEDto exchangeE2EEPublicKeys(E2EEDto e2eeDto) throws Exception;
    String encrypt(String plainText) throws Exception;
    String decrypt(String encryptedText) throws Exception;
}
