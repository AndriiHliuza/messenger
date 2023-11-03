package com.app.messenger.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;

public interface UserService {
    UserDto getUserByUniqueName(String uniqueName) throws UserNotFoundException;
}
