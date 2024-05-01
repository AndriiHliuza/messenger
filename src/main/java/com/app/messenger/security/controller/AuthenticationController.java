package com.app.messenger.security.controller;

import com.app.messenger.security.controller.dto.*;
import com.app.messenger.security.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ResponseStatus(HttpStatus.OK)
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public RegistrationResponse register(
            @Valid @RequestBody RegistrationRequest registrationRequest
    ) throws Exception {
        return authenticationService.register(registrationRequest);
    }

    @PostMapping("/authentication")
    public AuthenticationResponse authenticate(
            @Valid @RequestBody AuthenticationRequest authenticationRequest
    ) throws Exception {
        return authenticationService.authenticate(authenticationRequest);
    }
}
