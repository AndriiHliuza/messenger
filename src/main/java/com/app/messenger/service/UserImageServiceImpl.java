package com.app.messenger.service;

import com.app.messenger.controller.dto.UserImageDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserImageRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.service.converter.UserImageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImageServiceImpl implements UserImageService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserImageConverter userImageConverter;
    @Override
    public UserImageDto getUserImage(String uniqueName) throws Exception {
        User user = userRepository
                .findByUniqueName(uniqueName)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + uniqueName + " not found")
                );
        UserImageDto userImageDto = userImageConverter.toDto(userImageRepository.findByUser(user));
        if (userImageDto == null) {
            userImageDto = UserImageDto
                    .builder()
                    .type("application/octet-stream")
                    .build();
        }
        return userImageDto;
    }
}
