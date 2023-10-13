package com.app.messenger.security.service;

import com.app.messenger.exception.TokenNotFoundException;
import com.app.messenger.exception.UserAlreadyExistsException;
import com.app.messenger.security.controller.dto.AuthenticationRequest;
import com.app.messenger.security.controller.dto.AuthenticationResponse;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<AuthenticationResponse> register(RegistrationRequest registrationRequest) throws UserAlreadyExistsException;
    ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) throws TokenNotFoundException;
}
