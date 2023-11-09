package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.model.Role;

import java.util.Collection;

public interface UserService {
    UserDto getUserByUniqueName(Role role, String uniqueName) throws UserNotFoundException;
    Collection<UserDto> getAllUsers(int page, int size, String order);
}
