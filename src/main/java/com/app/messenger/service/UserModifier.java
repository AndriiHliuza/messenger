package com.app.messenger.service;

import com.app.messenger.controller.dto.UserImageDto;
import com.app.messenger.controller.dto.UserModificationRequest;
import com.app.messenger.repository.UserImageRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.repository.model.UserImage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserModifier implements Modifier<UserModificationRequest, User> {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final MultipartFileToUserImageDtoConverter multipartFileToUserImageDtoConverter;
    private final UserImageConverter userImageConverter;

    @Override
    public User modify(@Valid UserModificationRequest userModificationRequest, User user) throws Exception {
        if (isNewPasswordValid(userModificationRequest.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(userModificationRequest.getPassword()));
        }

        if (isNewValueValid(userModificationRequest.getUniqueName(), user.getUniqueName())) {
            if (userRepository.existsByUniqueName(userModificationRequest.getUniqueName())) {
                throw new IllegalArgumentException("User with uniqueName " + userModificationRequest.getUniqueName() + " already exists");
            }
            user.setUniqueName(userModificationRequest.getUniqueName());
        }

        if (isNewValueValid(userModificationRequest.getFirstname(), user.getFirstname())) {
            user.setFirstname(userModificationRequest.getFirstname());
        }

        if (isNewValueValid(userModificationRequest.getLastname(), user.getLastname())) {
            user.setLastname(userModificationRequest.getLastname());
        }

        user.setBirthday(userModificationRequest.getBirthday());

        UserImage oldUserImage = user.getUserImage();
        UserImage newUserImage = convertUserImageToEntity(userModificationRequest.getUserImage(), user);
        if (areUserImagesDifferent(newUserImage, oldUserImage)) {
            if (oldUserImage != null) {
                user.setUserImage(null);
                userImageRepository.delete(oldUserImage);
            }
            if (newUserImage != null) {
                user.setUserImage(newUserImage);
            }
        }

        return user;
    }

    private boolean isNewValueValid(String newValue, String currentValue) {
        return newValue != null &&
                !newValue.isBlank() &&
                !newValue.equals(currentValue);
    }

    private boolean isNewPasswordValid(String newPassword, String currentPassword) {
        return newPassword != null &&
                !newPassword.isBlank() &&
                !passwordEncoder.matches(newPassword, currentPassword);
    }

    private boolean areUserImagesDifferent(UserImage newUserImage, UserImage oldUserImage) {
        boolean areUserImagesDifferent = false;
        if (newUserImage != null && oldUserImage != null) {
            byte[] newUserImageData = newUserImage.getData();
            User newUserImageUser = newUserImage.getUser();

            byte[] oldUserImageData = oldUserImage.getData();
            User oldUserImageUser = oldUserImage.getUser();
            if (!Arrays.equals(newUserImageData, oldUserImageData) &&
                    newUserImageUser != null &&
                    oldUserImageUser != null
            ) {
                String newUserImageUsername = newUserImageUser.getUsername();
                String oldUserImageUsername = oldUserImageUser.getUsername();
                if (newUserImageUsername.equals(oldUserImageUsername)) {
                    areUserImagesDifferent = true;
                }
            }
        } else if (newUserImage != null || oldUserImage != null) {
            areUserImagesDifferent = true;
        }
        return areUserImagesDifferent;
    }

    private UserImage convertUserImageToEntity(MultipartFile image, User user) throws Exception {
        UserImageDto userImageDto = multipartFileToUserImageDtoConverter.apply(image);
        if (userImageDto != null) {
            userImageDto.setUsername(user.getUsername());
        }
        return userImageConverter.toEntity(userImageDto);
    }
}
