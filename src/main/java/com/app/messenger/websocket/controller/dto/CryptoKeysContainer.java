package com.app.messenger.websocket.controller.dto;

import lombok.*;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoKeysContainer {
    private PrivateKey serverPrivateKey;
    private PublicKey serverPublicKey;
    private PublicKey userPublicKey;
}
