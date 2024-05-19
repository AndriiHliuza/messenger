package com.app.messenger.service.converter;

import com.app.messenger.controller.dto.UserImageDto;
import com.app.messenger.exception.InvalidImageTypeException;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.ImageType;
import com.app.messenger.repository.model.User;
import com.app.messenger.repository.model.UserImage;
import com.app.messenger.service.CompressionUtil;
import com.app.messenger.service.converter.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImageConverter implements Converter<UserImageDto, UserImage> {

    private final UserRepository userRepository;
    private final CompressionUtil compressionUtil;

    @Override
    public UserImageDto toDto(UserImage userImage) throws Exception {
        UserImageDto userImageDto = null;
        if (userImage != null) {
            User user = userImage.getUser();
            if (user == null) {
                throw new UserNotFoundException("User not found for image with name " + userImage.getName());
            }
            userImageDto = UserImageDto
                    .builder()
                    .name(userImage.getName())
                    .type(userImage.getType().getValue())
                    .data(compressionUtil.decompressByteArray(userImage.getData()))
                    .username(user.getUsername())
                    .build();
        }
        return userImageDto;
    }

    @Override
    public UserImage toEntity(UserImageDto userImageDto) throws Exception {
        UserImage userImage = null;
        if (userImageDto != null) {
            ImageType imageType = ImageType.getImageTypeByValue(userImageDto.getType());
            String username = userImageDto.getUsername();
            if (imageType == null) {
                throw new InvalidImageTypeException("Unknown image type " + userImageDto.getType() + " provided");
            }
            User user = userRepository
                    .findByUsername(username)
                    .orElseThrow(
                            () -> new UserNotFoundException("User with username " + username + " not found")
                    );

            userImage = UserImage
                    .builder()
                    .name(userImageDto.getName())
                    .type(imageType)
                    .data(compressionUtil.compressByteArray(userImageDto.getData()))
                    .user(user)
                    .build();
        }
        return userImage;
    }
}
