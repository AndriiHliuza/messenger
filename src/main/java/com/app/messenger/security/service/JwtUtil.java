package com.app.messenger.security.service;

import com.app.messenger.repository.JwtRepository;
import com.app.messenger.repository.model.Jwt;
import com.app.messenger.repository.model.TokenTargetType;
import com.app.messenger.repository.model.TokenType;
import com.app.messenger.repository.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JwtUtil {

    @Value("${application.jwt.secret-encryption-key}")
    private String SECRET_ENCRYPTION_KEY;

    @Value("${application.jwt.refresh-token.expiration-date}")
    private int REFRESH_TOKEN_EXPIRATION_DATE;

    @Value("${application.jwt.access-token.expiration-date}")
    private int ACCESS_TOKEN_EXPIRATION_DATE;

    private final JwtRepository jwtRepository;
    private final EncryptionService encryptionService;

    public Jwt generateToken(User user, TokenTargetType targetType) throws Exception {
        Jwt token = jwtRepository
                .findByUserIdAndTargetType(user.getId(), targetType)
                .orElse(null);

        if (token != null) {
            jwtRepository.delete(token);
            log.debug("Old {} TOKEN for user with username {} was removed from database",
                    targetType.name(),
                    user.getUsername());
        }

        String plainContent = switch (targetType) {
            case ACCESS -> generateAccessToken(user);
            case REFRESH -> generateRefreshToken(user);
        };

        String encryptedContent = encryptionService.encrypt(plainContent);

        token = Jwt
                .builder()
                .content(encryptedContent)
                .type(TokenType.BEARER)
                .targetType(targetType)
                .user(user)
                .plainContent(plainContent)
                .build();

        if (jwtRepository.existsByContent(encryptedContent)) {
            jwtRepository.delete(token);
            log.debug("Old {} TOKEN for user with username {} was removed from database",
                    targetType.name(),
                    user.getUsername());
        }

        log.debug("{} TOKEN was generated for the user with username: {}",
                targetType.name(),
                user.getUsername());

        return token;
    }

    public String generateAccessToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("ROLE", user.getRole().name());
        extraClaims.put("TARGET", TokenTargetType.ACCESS.name());

        return buildToken(extraClaims, user, ACCESS_TOKEN_EXPIRATION_DATE);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("TARGET", TokenTargetType.REFRESH.name());

        return buildToken(extraClaims, user, REFRESH_TOKEN_EXPIRATION_DATE);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            int expirationTime
    ) {
        final Date creationDate = new Date();
        final Date expirationDate = new Date(creationDate.getTime() + expirationTime);

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) throws Exception {
        final String username = extractUsername(jwt);
        final TokenTargetType tokenTargetType = extractTokenTargetType(jwt);

        if (tokenTargetType == null) {
            return false;
        }

        Jwt userJwt = jwtRepository
                .findByUserIdAndTargetType(((User) userDetails).getId(), tokenTargetType)
                .orElse(null);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(jwt)
                && (userJwt != null)
                && encryptionService.matches(jwt, userJwt.getContent());
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    private TokenTargetType extractTokenTargetType(String jwt) {
        String targetClaim = extractClaim(jwt, claims -> claims.get("TARGET", String.class));
        if (targetClaim != null) {
            return TokenTargetType.valueOf(targetClaim.toUpperCase());
        }
        return null;
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
