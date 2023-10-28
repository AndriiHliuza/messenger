package com.app.messenger.security.service;

import com.app.messenger.security.controller.dto.AuthenticationRequest;
import com.app.messenger.security.controller.dto.AuthenticationResponse;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    AuthenticationResponse register(RegistrationRequest registrationRequest) throws Exception;
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws Exception;
}
