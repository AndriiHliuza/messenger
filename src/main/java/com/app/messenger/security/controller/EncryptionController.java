package com.app.messenger.security.controller;

import com.app.messenger.security.controller.dto.E2EEDto;
import com.app.messenger.security.service.E2EEService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ResponseStatus(HttpStatus.OK)
public class EncryptionController {

    private final E2EEService e2eeService;
    @PostMapping("/encryptionKeys/publicKey")
    public E2EEDto exchangeE2EEPublicKeys(@RequestBody E2EEDto e2eeDto) throws Exception {
        return e2eeService.exchangeE2EEPublicKeys(e2eeDto);
    }
}
