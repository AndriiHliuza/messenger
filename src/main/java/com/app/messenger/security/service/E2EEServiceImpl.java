package com.app.messenger.security.service;

import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.E2EEDto;
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
public class E2EEServiceImpl implements E2EEService {
    private final AuthenticationService authenticationService;
    private final ConcurrentHashMap<UUID, CryptoKeysContainer> cryptoKeysContainers = new ConcurrentHashMap<>(); // userId cryptoKeysContainer

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @Override
    public E2EEDto exchangeE2EEPublicKeys(E2EEDto e2eeDto) throws Exception {
        User currentUser = authenticationService.getCurrentUser();

        KeyPair serverKeyPair = generateKeyPair();
        PrivateKey serverPrivateKey = serverKeyPair.getPrivate();
        PublicKey serverPublicKey = serverKeyPair.getPublic();
        PublicKey userPublicKey = convertStringToPublicKeyAndGet(e2eeDto.getPublicKey());
        CryptoKeysContainer cryptoKeysContainer = CryptoKeysContainer
                .builder()
                .serverPrivateKey(serverPrivateKey)
                .serverPublicKey(serverPublicKey)
                .userPublicKey(userPublicKey)
                .build();
        cryptoKeysContainers.put(currentUser.getId(), cryptoKeysContainer);

        return E2EEDto
                .builder()
                .publicKey(Base64
                        .getEncoder()
                        .encodeToString(serverPublicKey.getEncoded())
                )
                .build();
    }

    private PublicKey convertStringToPublicKeyAndGet(String publicKeyString) throws Exception {
        publicKeyString = publicKeyString.trim().replaceAll("\\s+", "");
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    private PublicKey getPublicKey() throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        return cryptoKeysContainers.get(currentUser.getId()).getUserPublicKey();
    }

    private PrivateKey getPrivateKey() throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        return cryptoKeysContainers.get(currentUser.getId()).getServerPrivateKey();
    }

    @Override
    public String encrypt(String plainText) throws Exception {
        PublicKey publicKey = getPublicKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String encryptedText) throws Exception {
        PrivateKey privateKey = getPrivateKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
}
