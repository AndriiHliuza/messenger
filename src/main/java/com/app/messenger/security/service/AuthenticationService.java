package com.app.messenger.security.service;

import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.AuthenticationRequest;
import com.app.messenger.security.controller.dto.AuthenticationResponse;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.security.exception.UserNotAuthenticatedException;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    AuthenticationResponse register(RegistrationRequest registrationRequest) throws Exception;
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws Exception;
    UserDetails getAuthenticatedUserUserDetailsFromSecurityContext();
    User getCurrentUser() throws UserNotAuthenticatedException;
}
