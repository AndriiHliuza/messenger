package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.controller.dto.UserImageDto;
import com.app.messenger.repository.model.User;
import com.app.messenger.repository.model.UserImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserConverter implements Converter<UserDto, User> {
    private final UserImageConverter userImageConverter;
    private final DateTimeFormatter dateTimeFormatter;
    @Override
    public UserDto toDto(User user) throws Exception {
        UserDto userDto = null;
        if (user != null) {
            UserImage userImage = user.getUserImage();
            UserImageDto userImageDto = userImageConverter.toDto(userImage);
            String birthdayToReturn = null;

            LocalDate birthday = user.getBirthday();
            if (birthday != null) {
                birthdayToReturn = birthday.format(dateTimeFormatter);
            }
            userDto = UserDto
                    .builder()
                    .username(user.getUsername())
                    .uniqueName(user.getUniqueName())
                    .registrationDate(user.getRegistrationDate().toString())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .birthday(birthdayToReturn)
                    .role(user.getRole())
                    .userImage(userImageDto)
                    .build();
        }

        return userDto;
    }

    @Override
    public User toEntity(UserDto userDto) throws Exception {
        UserImageDto userImageDto = userDto.getUserImage();
        UserImage userImage = userImageConverter.toEntity(userImageDto);
        ZonedDateTime registrationDate = ZonedDateTime.parse(userDto.getRegistrationDate());

        LocalDate birthday = null;
        String receivedBirthday = userDto.getBirthday();
        if (receivedBirthday != null && !receivedBirthday.isBlank()) {
            birthday = LocalDate.parse(receivedBirthday, dateTimeFormatter);
        }

        return User
                .builder()
                .username(userDto.getUsername())
                .uniqueName(userDto.getUniqueName())
                .registrationDate(registrationDate)
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .birthday(birthday)
                .role(userDto.getRole())
                .userImage(userImage)
                .build();
    }
}
