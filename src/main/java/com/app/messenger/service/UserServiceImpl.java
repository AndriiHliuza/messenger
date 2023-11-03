package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    @Override
    public UserDto getUserByUniqueName(String uniqueName) throws UserNotFoundException {
        User user =  userRepository
                .findByUniqueName(uniqueName)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + uniqueName + " not found")
                );
        return userConverter.toDto(user);
    }
}
