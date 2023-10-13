package com.app.messenger.security.service;

import com.app.messenger.exception.TokenNotFoundException;
import com.app.messenger.repository.JwtRepository;
import com.app.messenger.repository.model.Jwt;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JwtService {

    @Value("${jwt.secret-encryption-key}")
    private String SECRET_ENCRYPTION_KEY;
    @Value("${jwt.expiration-date}")
    private int EXPIRATION_DATE;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtRepository jwtRepository;

//    public Jwt encodeJwt(String jwtContent, User user) {
//        return Jwt
//                .builder()
//                .content(passwordEncoder.encode(jwtContent))
//                .type(TokenType.BEARER)
//                .user(user)
//                .build();
//    }

    public void checkIfJwtExistsInDatabase(String rawJwtContent, String encryptedJwtContent) throws TokenNotFoundException {
        Jwt jwt = jwtRepository.findByContent(encryptedJwtContent).orElseThrow(
                () -> new TokenNotFoundException("Jwt not found in database")
        );

        if (!passwordEncoder.matches(rawJwtContent, jwt.getContent())) {
            throw new TokenNotFoundException("Jwt do not match to the encrypted jwt");
        }
    }

    public Jwt generateJwt(User user) {
        Jwt jwt = jwtRepository
                .findByUserId(user.getId())
                .orElse(null);

        if (jwt != null) {
            jwtRepository.delete(jwt);
            log.debug("Old jwt for user with username {} was removed from database", user.getUsername());
        }

        String plainContent = generateToken(user);
        String encodedContent = passwordEncoder.encode(plainContent);
        jwt = Jwt
                .builder()
                .content(encodedContent)
                .type(TokenType.BEARER)
                .user(user)
                .plainContent(plainContent)
                .build();

        if (jwtRepository.existsByContent(encodedContent)) {
            jwtRepository.delete(jwt);
            log.debug("Old jwt for user with username {} was removed from database", user.getUsername());
        }

        log.debug("Jwt was generated for the user with username: {}", user.getUsername());

        return jwt;
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        final Date creationDate = new Date();
        final Date expirationDate = new Date(creationDate.getTime() + EXPIRATION_DATE);

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        Jwt userJwt = ((User) userDetails).getJwt();
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(jwt)
                && (userJwt != null)
                && passwordEncoder.matches(jwt, userJwt.getContent());
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
