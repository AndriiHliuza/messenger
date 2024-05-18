package com.app.messenger.security.handler;

import com.app.messenger.security.exception.*;
import com.app.messenger.handler.dto.ExceptionResponse;
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

    @ExceptionHandler(value = {
            UserAccountNotActivatedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleUserAccountNotActivatedException() {
        return ExceptionResponse
                .builder()
                .message("User account is not activated")
                .build();
    }

    @ExceptionHandler(value = {
            UserAccountBlockedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleUserAccountBlockedException() {
        return ExceptionResponse
                .builder()
                .message("User account is blocked")
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
            UserDeletionException.class
    })
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ExceptionResponse handleUserDeletionException() {
        return ExceptionResponse
                .builder()
                .message("Forbidden operation")
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

    @ExceptionHandler(value = {
            EncryptionKeyNotFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleE2EEExceptions() {
        return ExceptionResponse
                .builder()
                .message("Encryption key is absent")
                .build();
    }
}
