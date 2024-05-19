package com.app.messenger.service.converter;

import com.app.messenger.controller.dto.UserAccountDto;
import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.User;
import com.app.messenger.repository.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class UserAccountConverter implements Converter<UserAccountDto, UserAccount> {
    private final UserConverter userConverter;
    private final UserRepository userRepository;

    @Override
    public UserAccountDto toDto(UserAccount userAccount) throws Exception {
        UserAccountDto userAccountDto = null;
        if (userAccount != null) {
            UserDto userDto = userConverter.toDto(userAccount.getUser());
            userAccountDto = UserAccountDto
                    .builder()
                    .userDto(userDto)
                    .state(userAccount.getState())
                    .createdAt(userAccount.getCreatedAt())
                    .activatedAt(userAccount.getActivatedAt())
                    .build();
        }
        return userAccountDto;
    }

    @Override
    public UserAccount toEntity(UserAccountDto userAccountDto) throws Exception {
        UserAccount userAccount = null;
        if (userAccountDto != null) {
            UserDto userDto = userAccountDto.getUserDto();
            if (userDto == null) {
                throw new IllegalArgumentException("UserDto is null");
            }
            String username = userDto.getUsername();
            User user = userRepository
                    .findByUsername(username)
                    .orElseThrow(
                            () -> new UserNotFoundException("user with username " + username + " not found")
                    );

            userAccount = UserAccount
                    .builder()
                    .user(user)
                    .state(userAccountDto.getState())
                    .createdAt(ZonedDateTime.now())
                    .activatedAt(ZonedDateTime.now())
                    .build();
        }

        return userAccount;
    }
}
