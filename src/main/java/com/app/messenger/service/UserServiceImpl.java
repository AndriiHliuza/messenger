package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    @Override
    public UserDto getUserByUniqueName(Role role, String uniqueName) throws UserNotFoundException {
        User user =  userRepository
                .findByRoleAndUniqueName(role, uniqueName)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + uniqueName + " not found")
                );
        return userConverter.toDto(user);
    }

    @Override
    public Collection<UserDto> getAllUsers(int page, int size, String order) {
        Sort.Direction sortOrder = Sort.Direction.valueOf(order.toUpperCase());
        Sort sort = Sort.by(sortOrder, "uniqueName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Collection<User> users = userRepository.findByRole(Role.USER, pageable);

        return users
                .stream()
                .map(userConverter::toDto)
                .collect(Collectors.toList());
    }
}
