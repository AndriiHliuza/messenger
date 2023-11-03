package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.repository.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserConverter implements Converter<UserDto, User> {
    @Override
    public UserDto toDto(User user) {
        return UserDto
                .builder()
                .username(user.getUsername())
                .uniqueName(user.getUniqueName())
                .registrationDate(user.getRegistrationDate())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .birthday(user.getBirthday())
                .role(user.getRole())
                .build();
    }

    @Override
    public User toEntity(UserDto userDto) throws Exception {
        return User
                .builder()
                .username(userDto.getUsername())
                .uniqueName(userDto.getUniqueName())
                .registrationDate(userDto.getRegistrationDate())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .birthday(userDto.getBirthday())
                .role(userDto.getRole())
                .build();
    }
}
