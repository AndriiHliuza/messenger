package com.app.messenger.security.handler;

import com.app.messenger.security.exception.PasswordNotFoundException;
import com.app.messenger.security.exception.PasswordNotValidException;
import com.app.messenger.security.exception.UserAlreadyExistsException;
import com.app.messenger.handler.dto.ExceptionResponse;
import com.app.messenger.security.exception.UserNotAuthenticatedException;
import io.jsonwebtoken.security.SignatureException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

@RestControllerAdvice
public class SecurityExceptionHandler {
    @ExceptionHandler(value = {
            UserAlreadyExistsException.class,
            PasswordNotFoundException.class,
            PasswordNotValidException.class,
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
                .message("Invalid jwt")
                .build();
    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            UserNotAuthenticatedException.class
    })
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleAuthenticationException() {
        return ExceptionResponse
                .builder()
                .message("Bad credentials")
                .build();
    }

    @ExceptionHandler(value = {
            IllegalBlockSizeException.class,
            BadPaddingException.class,
            InvalidKeyException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleEncryptionExceptions() {
        return ExceptionResponse
                .builder()
                .message("Request failed due to server side error")
                .build();
    }
}
