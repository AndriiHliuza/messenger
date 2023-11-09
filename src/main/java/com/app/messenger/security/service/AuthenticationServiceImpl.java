package com.app.messenger.security.service;

import com.app.messenger.security.exception.UserAlreadyExistsException;
import com.app.messenger.repository.JwtRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Jwt;
import com.app.messenger.repository.model.TokenTargetType;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.AuthenticationRequest;
import com.app.messenger.security.controller.dto.AuthenticationResponse;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.service.UserRegistrationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JwtRepository jwtRepository;
    private final UserRegistrationConverter userRegistrationConverter;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegistrationRequest registrationRequest) throws Exception {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + registrationRequest.getUsername() + " already exists in database");
        } else if (userRepository.existsByUniqueName(registrationRequest.getUniqueName())) {
            throw new UserAlreadyExistsException("User with uniqueName " + registrationRequest.getUniqueName() + " already exists in database");
        }

        User userToSave = userRegistrationConverter.toEntity(registrationRequest);
        User savedUser = userRepository.save(userToSave);
        log.debug("New user with username: {} was saved to database", savedUser.getUsername());

        return buildAuthenticationResponse(savedUser);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        User user = userRepository
                .findByUsername(authenticationRequest.getUsername())
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                        "User with username: "
                                        + authenticationRequest.getUsername() +
                                        "was not found in database"
                        )
                );

        return buildAuthenticationResponse(user);
    }

    private AuthenticationResponse buildAuthenticationResponse(User user) throws Exception {
        Jwt accessToken = jwtUtil.generateToken(user, TokenTargetType.ACCESS);
        Jwt refreshToken = jwtUtil.generateToken(user, TokenTargetType.REFRESH);

        jwtRepository.save(accessToken);
        log.debug("New ACCESS TOKEN for user with username: {} was saved to database", user.getUsername());
        jwtRepository.save(refreshToken);
        log.debug("New REFRESH TOKEN for user with username: {} was saved to database", user.getUsername());

        return AuthenticationResponse
                .builder()
                .accessToken(accessToken.getPlainContent())
                .refreshToken(refreshToken.getPlainContent())
                .build();
    }

    public UserDetails getAuthenticatedUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = null;
        if (authentication != null) {
            authenticatedUser = (UserDetails) authentication.getPrincipal();
        }
        return authenticatedUser;
    }
}
