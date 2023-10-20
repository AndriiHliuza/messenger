package com.app.messenger.security.controller;

import com.app.messenger.security.controller.dto.AuthenticationRequest;
import com.app.messenger.security.controller.dto.AuthenticationResponse;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegistrationRequest registrationRequest
    ) throws Exception {
        return authenticationService.register(registrationRequest);
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ) {
        return authenticationService.authenticate(authenticationRequest);
    }
}
