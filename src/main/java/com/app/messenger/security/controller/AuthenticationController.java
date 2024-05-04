package com.app.messenger.security.controller;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.email.dto.EmailDto;
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

    @PostMapping("/user/{username}/account/activation/email")
    public EmailDto sendEmailForUserAccountActivation(
            @PathVariable String username,
            @Valid @RequestBody EmailDto emailDto) throws Exception {
        return authenticationService.sendEmailForUserAccountActivation(username, emailDto);
    }

    @PostMapping("/user/{username}/account/activation")
    public UserDto activateUserAccount(
            @PathVariable String username,
            @RequestBody UserAccountActivationRequest userAccountActivationRequest) throws Exception {
        return authenticationService.activateUserAccount(username, userAccountActivationRequest);
    }
}
