package com.app.messenger.security.service;

import com.app.messenger.security.controller.dto.AuthenticationRequest;
import com.app.messenger.security.controller.dto.AuthenticationResponse;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<AuthenticationResponse> register(RegistrationRequest registrationRequest) throws Exception;
    ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) throws Exception;
    void getNewAccessToken(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
