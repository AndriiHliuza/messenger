package com.app.messenger.security.service;

import com.app.messenger.exception.TokenNotFoundException;
import com.app.messenger.exception.UserAlreadyExistsException;
import com.app.messenger.repository.JwtRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Jwt;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.AuthenticationRequest;
import com.app.messenger.security.controller.dto.AuthenticationResponse;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.service.UserRegistrationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<AuthenticationResponse> register(RegistrationRequest registrationRequest)
            throws UserAlreadyExistsException {

        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + registrationRequest.getUsername() + " already exists in database");
        }

        User userToSave = userRegistrationConverter.toEntity(registrationRequest);
        Jwt jwt = jwtService.generateJwt(userToSave);

        User savedUser = userRepository.save(userToSave);
        log.debug("New user with username: {} was saved to database", savedUser.getUsername());

        jwtRepository.save(jwt);
        log.debug("New jwt for user with username: {} was saved to database", savedUser.getUsername());

        return ResponseEntity.ok(
                AuthenticationResponse
                        .builder()
                        .jwt(jwt.getPlainContent())
                        .build()
        );
    }

    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) throws TokenNotFoundException {
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

        Jwt jwt = jwtService.generateJwt(user);
        jwtRepository.save(jwt);
        log.debug("New jwt for user with username: {} was saved to database", user.getUsername());

        return ResponseEntity.ok(
                AuthenticationResponse
                        .builder()
                        .jwt(jwt.getPlainContent())
                        .build()
        );
    }
}
