package com.app.messenger.security.service;

import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.EncryptionKeyDto;
import com.app.messenger.security.exception.EncryptionKeyNotFoundException;
import com.app.messenger.websocket.controller.dto.CryptoKeysContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AsymmetricEncryptionServiceImpl implements EncryptionService {
    private final String ALGORITHM = "RSA";
    private final AuthenticationService authenticationService;
    private final ConcurrentHashMap<UUID, CryptoKeysContainer> cryptoKeysContainers = new ConcurrentHashMap<>(); // userId cryptoKeysContainer

    @Override
    public String encrypt(String rawData) throws Exception {
        PublicKey publicKey = getPublicKey();
        if (publicKey == null) {
            throw new EncryptionKeyNotFoundException("User's public encryption key does not exist");
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(rawData.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        PrivateKey privateKey = getPrivateKey();
        if (privateKey == null) {
            throw new EncryptionKeyNotFoundException("Server's private encryption key does not exist");
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    @Override
    public boolean matches(String rawData, String encryptedData) throws Exception {
        String encryptedRawData = encrypt(rawData);
        return encryptedRawData.equals(encryptedData);
    }

    public EncryptionKeyDto exchangePublicKeys(EncryptionKeyDto encryptionKeyDto) throws Exception {
        User currentUser = authenticationService.getCurrentUser();

        KeyPair serverKeyPair = generateKeyPair();
        PrivateKey serverPrivateKey = serverKeyPair.getPrivate();
        PublicKey serverPublicKey = serverKeyPair.getPublic();
        PublicKey userPublicKey = convertStringToPublicKeyAndGet(encryptionKeyDto.getEncryptionKey());
        CryptoKeysContainer cryptoKeysContainer = CryptoKeysContainer
                .builder()
                .serverPrivateKey(serverPrivateKey)
                .serverPublicKey(serverPublicKey)
                .userPublicKey(userPublicKey)
                .build();
        cryptoKeysContainers.put(currentUser.getId(), cryptoKeysContainer);

        return EncryptionKeyDto
                .builder()
                .encryptionKey(Base64
                        .getEncoder()
                        .encodeToString(serverPublicKey.getEncoded())
                )
                .build();
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private PublicKey convertStringToPublicKeyAndGet(String publicKeyString) throws Exception {
        publicKeyString = publicKeyString.trim().replaceAll("\\s+", "");
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    private PublicKey getPublicKey() throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        CryptoKeysContainer cryptoKeysContainer = cryptoKeysContainers.get(currentUser.getId());
        return cryptoKeysContainer != null ? cryptoKeysContainer.getUserPublicKey() : null;
    }

    private PrivateKey getPrivateKey() throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        CryptoKeysContainer cryptoKeysContainer = cryptoKeysContainers.get(currentUser.getId());
        return cryptoKeysContainer != null ? cryptoKeysContainer.getServerPrivateKey() : null;
    }
}
