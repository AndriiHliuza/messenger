package com.app.messenger.service;

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
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @Override
    public RegistrationRequest toDto(User user) {
        return RegistrationRequest
                .builder()
                .username(user.getUsername())
                .firstName(user.getFirstname())
                .lastName(user.getFirstname())
                .birthday(user.getBirthday().format(dateTimeFormatter))
                .build();
    }

    @Override
    public User toEntity(RegistrationRequest registrationRequest) {

        return User
                .builder()
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .registrationDate(ZonedDateTime.now())
                .firstname(registrationRequest.getFirstName())
                .lastname(registrationRequest.getLastName())
                .birthday(LocalDate.parse(registrationRequest.getBirthday(), dateTimeFormatter))
                .role(Role.USER)
                .build();
    }
}
