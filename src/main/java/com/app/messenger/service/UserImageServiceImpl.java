package com.app.messenger.service;

import com.app.messenger.controller.dto.UserImageDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserImageRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
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
        return userImageConverter.toDto(userImageRepository.findByUser(user));
    }
}
