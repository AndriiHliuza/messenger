package com.app.messenger.websocket.config;

import com.app.messenger.exception.InvalidTokenException;
import com.app.messenger.security.service.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        List<StompCommand> stompCommands = List.of(
                StompCommand.CONNECT,
                StompCommand.SUBSCRIBE,
                StompCommand.SEND
        );

        if (accessor == null) {
            throw new IllegalArgumentException("Accessor is null");
        }
        StompCommand receivedStompCommand = accessor.getCommand();
        if (stompCommands.contains(receivedStompCommand)) {
            boolean isJwtValid = false;
            List<String> authorizationHeader = accessor.getNativeHeader("Authorization");
            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                String authorizationHeaderValue = authorizationHeader.get(0);
                if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("Bearer ")) {
                    String jwt = authorizationHeaderValue.substring(7);
                    if (!jwt.isBlank()) {
                        String username = jwtUtil.extractUsername(jwt);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (userDetails != null) {
                            try {
                                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                                    isJwtValid = true;
                                }
                            } catch (Exception e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    }
                }
            }

            if (!isJwtValid) {
                throw new InvalidTokenException("Jwt is invalid");
            }
        }
        return message;
    }
}
