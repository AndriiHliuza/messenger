package com.app.messenger.controller;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.model.Role;
import com.app.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ResponseStatus(HttpStatus.OK)
public class AdminController {

    private final UserService userService;
    @GetMapping("/admin/{uniqueName}")
    @PreAuthorize("hasAuthority('READ_ADMIN')")
    public UserDto getAdminByUniqueName(@PathVariable String uniqueName) throws UserNotFoundException {
        return userService.getUserByUniqueName(Role.ADMIN, uniqueName);
    }

    @GetMapping("/root/{uniqueName}")
    @PreAuthorize("hasAuthority('READ_ROOT')")
    public UserDto getRootByUniqueName(@PathVariable String uniqueName) throws UserNotFoundException {
        return userService.getUserByUniqueName(Role.ROOT, uniqueName);
    }
}
