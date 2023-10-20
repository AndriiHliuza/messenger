package com.app.messenger.service;

import com.app.messenger.exception.PasswordNotFoundException;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserRegistrationConverter implements Converter<RegistrationRequest, User> {

    private final PasswordEncoder passwordEncoder;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    public RegistrationRequest toDto(User user) {
        String birthdayToReturn = null;

        LocalDate birthday = user.getBirthday();
        if (birthday != null) {
            birthdayToReturn = birthday.format(dateTimeFormatter);
        }

        return RegistrationRequest
                .builder()
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getFirstname())
                .birthday(birthdayToReturn)
                .build();
    }

    @Override
    public User toEntity(RegistrationRequest registrationRequest) throws Exception {
        String password = registrationRequest.getPassword();
        if (password == null) {
            throw new PasswordNotFoundException("password is null");
        }

        LocalDate birthday = null;
        String receivedBirthday = registrationRequest.getBirthday();
        if (receivedBirthday != null) {
            birthday = LocalDate.parse(receivedBirthday, dateTimeFormatter);
        }

        return User
                .builder()
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .registrationDate(ZonedDateTime.now())
                .firstname(registrationRequest.getFirstname())
                .lastname(registrationRequest.getLastname())
                .birthday(birthday)
                .role(Role.USER)
                .build();
    }
}
