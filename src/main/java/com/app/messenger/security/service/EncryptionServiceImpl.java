package com.app.messenger.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {
    private final Cipher cipher;
    private final SecretKey secretKey;

    public EncryptionServiceImpl(
            @Value("${application.security.service.encryption.algorithm}") String encryptionAlgorithm,
            @Value("${application.security.service.encryption.aes-key}") String encryptionKey
    ) throws Exception {
        cipher = Cipher.getInstance(encryptionAlgorithm);
        secretKey = new SecretKeySpec(encryptionKey.getBytes(), encryptionAlgorithm);
    }

    public String encrypt(String value) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedValueBytes = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encryptedValueBytes);
    }

    public String decrypt(String encryptedValue) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedValueBytes = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedValueBytes = cipher.doFinal(encryptedValueBytes);
        return new String(decryptedValueBytes);
    }

    public boolean matches(String rawValue, String encryptedValue) throws Exception {
        String encryptedRawValue = encrypt(rawValue);
        return encryptedRawValue.equals(encryptedValue);
    }
}
