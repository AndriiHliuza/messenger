package com.app.messenger.controller;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.model.Role;
import com.app.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@ResponseStatus(HttpStatus.OK)
public class UserController {

    private final UserService userService;

    @GetMapping("/{uniqueName}")
    @PreAuthorize("hasAuthority('READ_USER')")
    public UserDto getUserByUniqueName(@PathVariable String uniqueName) throws UserNotFoundException {
        return userService.getUserByUniqueName(Role.USER, uniqueName);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_USER')")
    public Collection<UserDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "ASC") String order
            ) {
        return userService.getAllUsers(page, size, order);
    }
}
