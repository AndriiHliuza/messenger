package com.app.messenger.security.service;

public interface EncryptionService {
    String encrypt(String rawData) throws Exception;
    String decrypt(String encryptedData) throws Exception;
    boolean matches(String rawData, String encryptedData) throws Exception;
}
