package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.model.Role;

public interface UserService {
    UserDto getUserByUniqueName(Role role, String uniqueName) throws UserNotFoundException;
}
