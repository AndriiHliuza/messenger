package com.app.messenger.security.service;

import com.app.messenger.exception.UserAlreadyExistsException;
import com.app.messenger.repository.JwtRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Jwt;
import com.app.messenger.repository.model.TokenTargetType;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.AccessToken;
import com.app.messenger.security.controller.dto.AuthenticationRequest;
import com.app.messenger.security.controller.dto.AuthenticationResponse;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.service.UserRegistrationConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<AuthenticationResponse> register(RegistrationRequest registrationRequest) throws Exception {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + registrationRequest.getUsername() + " already exists in database");
        }

        User userToSave = userRegistrationConverter.toEntity(registrationRequest);
        User savedUser = userRepository.save(userToSave);
        log.debug("New user with username: {} was saved to database", savedUser.getUsername());

        return buildAuthenticationResponse(savedUser);
    }

    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) throws Exception {
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

    public void getNewAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        final String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String username;

        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")) {
            jwt = authenticationHeader.substring(7);
            username = jwtService.extractUsername(jwt);

            if (username != null) {
                User user = userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("User with username " + username + " not found")
                        );

                if (jwtService.isTokenValid(jwt, user)) {
                    Jwt accessJwt = jwtService.generateToken(user, TokenTargetType.ACCESS);
                    jwtRepository.save(accessJwt);
                    AccessToken accessToken = AccessToken
                            .builder()
                            .accessToken(accessJwt.getPlainContent())
                            .build();
                    objectMapper.writeValue(response.getOutputStream(), accessToken);
                }
            }
        }
    }

    private ResponseEntity<AuthenticationResponse> buildAuthenticationResponse(User user) throws Exception {
        Jwt accessToken = jwtService.generateToken(user, TokenTargetType.ACCESS);
        Jwt refreshToken = jwtService.generateToken(user, TokenTargetType.REFRESH);

        jwtRepository.save(accessToken);
        log.debug("New ACCESS TOKEN for user with username: {} was saved to database", user.getUsername());
        jwtRepository.save(refreshToken);
        log.debug("New REFRESH TOKEN for user with username: {} was saved to database", user.getUsername());

        return ResponseEntity.ok(
                AuthenticationResponse
                        .builder()
                        .accessToken(accessToken.getPlainContent())
                        .refreshToken(refreshToken.getPlainContent())
                        .build()
        );
    }
}
