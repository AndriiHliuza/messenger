package com.app.messenger.security.service;

import com.app.messenger.repository.JwtRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Jwt;
import com.app.messenger.repository.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {
    private final JwtRepository jwtRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwtContent;
        final String username;
        final User user;
        final List<Jwt> tokens;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtContent = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwtContent);

            if (username != null) {
                user = userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("User with username " + username + " not found")
                        );

                try {
                    if (user != null && jwtUtil.isTokenValid(jwtContent, user)) {
                        tokens = jwtRepository.findByUserId(user.getId());

                        if (!tokens.isEmpty()) {
                            jwtRepository.deleteAll(tokens);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
