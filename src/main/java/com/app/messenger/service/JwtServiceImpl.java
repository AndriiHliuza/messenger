package com.app.messenger.service;

import com.app.messenger.controller.dto.TokenValidationResponse;
import com.app.messenger.repository.JwtRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Jwt;
import com.app.messenger.repository.model.TokenTargetType;
import com.app.messenger.repository.model.User;
import com.app.messenger.controller.dto.Token;
import com.app.messenger.security.service.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final JwtUtil jwtUtil;
    private final JwtRepository jwtRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void getNewAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        final String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String username;

        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")) {
            jwt = authenticationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);

            if (username != null) {
                User user = userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("User with username " + username + " not found")
                        );

                if (jwtUtil.isTokenValid(jwt, user)) {
                    Jwt accessJwt = jwtUtil.generateToken(user, TokenTargetType.ACCESS);
                    jwtRepository.save(accessJwt);
                    Token accessToken = Token
                            .builder()
                            .content(accessJwt.getPlainContent())
                            .build();
                    objectMapper.writeValue(response.getOutputStream(), accessToken);
                }
            }
        }
    }

    @Override
    public TokenValidationResponse validate(Token token) throws Exception {
        final String jwt = token.getContent();
        final String username = jwtUtil.extractUsername(token.getContent());

        boolean isValid = false;

        if (username != null) {
            User user = userRepository
                    .findByUsername(username)
                    .orElseThrow(
                            () -> new UsernameNotFoundException("User with username " + username + " not found")
                    );
            isValid = jwtUtil.isTokenValid(jwt, user);
        }
        return TokenValidationResponse
                .builder()
                .token(token.getContent())
                .isValid(isValid)
                .build();
    }
}
