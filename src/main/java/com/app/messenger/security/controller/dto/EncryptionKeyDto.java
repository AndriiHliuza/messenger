package com.app.messenger.security.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionKeyDto {
    private String encryptionKey;
}
