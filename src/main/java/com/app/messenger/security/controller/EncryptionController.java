package com.app.messenger.security.controller;

import com.app.messenger.security.controller.dto.EncryptionKeyDto;
import com.app.messenger.security.service.AsymmetricEncryptionServiceImpl;
import com.app.messenger.security.service.SymmetricEncryptionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/encryptionKeys")
@ResponseStatus(HttpStatus.OK)
public class EncryptionController {

    private final AsymmetricEncryptionServiceImpl asymmetricEncryptionServiceImpl;
    private final SymmetricEncryptionServiceImpl symmetricEncryptionServiceImpl;

    @GetMapping("/aesKey")
    public EncryptionKeyDto getEncryptedAesKey(@RequestParam(defaultValue = "256") int keySize) throws Exception {
        return symmetricEncryptionServiceImpl.getEncryptedAesKey(keySize);
    }

    @PostMapping("publicKey")
    public EncryptionKeyDto exchangePublicKeys(@RequestBody EncryptionKeyDto encryptionKeyDto) throws Exception {
        return asymmetricEncryptionServiceImpl.exchangePublicKeys(encryptionKeyDto);
    }
}
