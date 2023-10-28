package com.app.messenger.security.service;

public interface EncryptionService {
    String encrypt(String value) throws Exception;
    String decrypt(String encryptedValue) throws Exception;
    boolean matches(String rawValue, String encryptedValue) throws Exception;
}
