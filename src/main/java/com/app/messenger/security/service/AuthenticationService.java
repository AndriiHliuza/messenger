package com.app.messenger.security.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.email.dto.EmailDto;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.*;
import com.app.messenger.security.exception.UserNotAuthenticatedException;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    RegistrationResponse register(RegistrationRequest registrationRequest) throws Exception;
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws Exception;
    EmailDto sendEmailForUserAccountActivation(String username, EmailDto emailDto) throws Exception;
    UserDto activateUserAccount(String username, UserAccountActivationRequest userAccountActivationRequest) throws Exception;
    UserDetails getAuthenticatedUserUserDetailsFromSecurityContext();
    User getCurrentUser() throws UserNotAuthenticatedException;
}
