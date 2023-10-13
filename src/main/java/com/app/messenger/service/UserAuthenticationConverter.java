package com.app.messenger.service;

import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationConverter implements Converter<AuthenticationRequest, User> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationRequest toDto(User user) {
        return AuthenticationRequest
                .builder()
                .username(user.getUsername())
                .build();
    }

    @Override
    public User toEntity(AuthenticationRequest authenticationRequest) {
        return User
                .builder()
                .username(authenticationRequest.getUsername())
                .password(passwordEncoder.encode(authenticationRequest.getPassword()))
                .build();
    }
}
