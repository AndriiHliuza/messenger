package com.app.messenger.service.converter;

import com.app.messenger.security.exception.PasswordNotFoundException;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.service.converter.Converter;
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
    private final DateTimeFormatter dateTimeFormatter;
    @Override
    public RegistrationRequest toDto(User user) throws Exception {
        String birthdayToReturn = null;

        LocalDate birthday = user.getBirthday();
        if (birthday != null) {
            birthdayToReturn = birthday.format(dateTimeFormatter);
        }

        return RegistrationRequest
                .builder()
                .username(user.getUsername())
                .uniqueName(user.getUniqueName())
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
        if (receivedBirthday != null && !receivedBirthday.isBlank()) {
            birthday = LocalDate.parse(receivedBirthday, dateTimeFormatter);
        }

        return User
                .builder()
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .uniqueName(registrationRequest.getUniqueName())
                .registrationDate(ZonedDateTime.now())
                .firstname(registrationRequest.getFirstname())
                .lastname(registrationRequest.getLastname())
                .birthday(birthday)
                .role(Role.USER)
                .build();
    }
}
