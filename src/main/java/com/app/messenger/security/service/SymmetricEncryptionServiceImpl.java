package com.app.messenger.security.service;

import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.EncryptionKeyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SymmetricEncryptionServiceImpl implements EncryptionService {
    private final AuthenticationService authenticationService;
    private final EncryptionService asymmetricEncryptionServiceImpl;
    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private final ConcurrentHashMap<UUID, SecretKey> encryptionKeysContainer = new ConcurrentHashMap<>();

    @Override
    public String encrypt(String rawData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromEncryptionKeysContainer());
        byte[] encryptedBytes = cipher.doFinal(rawData.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getKeyFromEncryptionKeysContainer());
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    @Override
    public boolean matches(String rawData, String encryptedData) throws Exception {
        String encryptedRawData = encrypt(rawData);
        return encryptedRawData.equals(encryptedData);
    }

    public EncryptionKeyDto getEncryptedAesKey(int size) throws Exception {
        SecretKey secretKey = generateKey(size);
        String convertedSecretKey = convertSecretKeyToString(secretKey);
        String encryptedSecretKey = encryptAesKeyUsingAsymmetricEncryption(convertedSecretKey);
        return EncryptionKeyDto
                .builder()
                .encryptionKey(encryptedSecretKey)
                .build();
    }

    private SecretKey generateKey(int size) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(size);
        SecretKey secretKey = keyGenerator.generateKey();

        User currentUser = authenticationService.getCurrentUser();
        encryptionKeysContainer.put(currentUser.getId(), secretKey);

        return secretKey;
    }

    private String encryptAesKeyUsingAsymmetricEncryption(String secretKey) throws Exception {
        return asymmetricEncryptionServiceImpl.encrypt(secretKey);
    }

    private SecretKey getKeyFromEncryptionKeysContainer() throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        return encryptionKeysContainer.get(currentUser.getId());
    }

    public String convertSecretKeyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public SecretKey convertStringToSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
}
