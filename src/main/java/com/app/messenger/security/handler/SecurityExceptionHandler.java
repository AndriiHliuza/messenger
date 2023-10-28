package com.app.messenger.security.handler;

import com.app.messenger.exception.PasswordNotFoundException;
import com.app.messenger.exception.UserAlreadyExistsException;
import com.app.messenger.security.handler.response.ExceptionResponse;
import io.jsonwebtoken.security.SignatureException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {
    @ExceptionHandler(value = {
            UserAlreadyExistsException.class,
            PasswordNotFoundException.class,
            PropertyValueException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleUsernameAndPasswordExceptions() {
        return ExceptionResponse
                .builder()
                .message("Invalid entries")
                .build();
    }

    @ExceptionHandler(value = {SignatureException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleJwtException() {
        return ExceptionResponse
                .builder()
                .message("Invalid jwt token")
                .build();
    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleAuthenticationException() {
        return ExceptionResponse
                .builder()
                .message("Bad credentials")
                .build();
    }

}
