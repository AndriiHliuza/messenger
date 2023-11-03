package com.app.messenger.controller;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@ResponseStatus(HttpStatus.OK)
public class UserController {

    private final UserService userService;

    @GetMapping("/{uniqueName}")
    public UserDto getUserByUniqueName(@PathVariable String uniqueName) throws UserNotFoundException {
        return userService.getUserByUniqueName(uniqueName);
    }
}
