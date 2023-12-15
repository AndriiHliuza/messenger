package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.controller.dto.UserImageDto;
import com.app.messenger.repository.model.User;
import com.app.messenger.repository.model.UserImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserConverter implements Converter<UserDto, User> {
    private final UserImageConverter userImageConverter;
    @Override
    public UserDto toDto(User user) throws Exception {
        UserImage userImage = user.getUserImage();
        UserImageDto userImageDto = userImageConverter.toDto(userImage);
        return UserDto
                .builder()
                .username(user.getUsername())
                .uniqueName(user.getUniqueName())
                .registrationDate(user.getRegistrationDate())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .birthday(user.getBirthday())
                .role(user.getRole())
                .userImage(userImageDto)
                .build();
    }

    @Override
    public User toEntity(UserDto userDto) throws Exception {
        UserImageDto userImageDto = userDto.getUserImage();
        UserImage userImage = userImageConverter.toEntity(userImageDto);
        return User
                .builder()
                .username(userDto.getUsername())
                .uniqueName(userDto.getUniqueName())
                .registrationDate(userDto.getRegistrationDate())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .birthday(userDto.getBirthday())
                .role(userDto.getRole())
                .userImage(userImage)
                .build();
    }
}
