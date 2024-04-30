package com.app.messenger.controller;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.repository.model.Role;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.security.controller.dto.RegistrationResponse;
import com.app.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ResponseStatus(HttpStatus.OK)
public class AdminController {

    private final UserService userService;

    @PostMapping("/admin")
    public RegistrationResponse createAdmin(
            @RequestBody RegistrationRequest registrationRequest
    ) throws Exception {
        return userService.createAdmin(registrationRequest);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('READ_ADMIN')")
    public UserDto getAdminByUsername(@RequestParam String username) throws Exception {
        return userService.getUserByUsername(Role.ADMIN, username);
    }

    @GetMapping("/admins/{uniqueName}")
    @PreAuthorize("hasAuthority('READ_ADMIN')")
    public UserDto getAdminByUniqueName(@PathVariable String uniqueName) throws Exception {
        return userService.getUserByUniqueName(Role.ADMIN, uniqueName);
    }

    @DeleteMapping("/admins/{username}")
    @PreAuthorize("hasAuthority('DELETE_ADMIN')")
    public UserDto deleteAdmin(@PathVariable String username) throws Exception {
        return userService.deleteUser(username);
    }

    @GetMapping("/admins")
    @PreAuthorize("hasAuthority('READ_ADMIN')")
    public Collection<UserDto> getAdminsPagedAndSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ASC") String order
    ) throws Exception {
        return userService.getUsersPagedAndSorted(page, size, order, Role.ADMIN);
    }

    @GetMapping("/root")
    @PreAuthorize("hasAuthority('READ_ROOT')")
    public UserDto getRootByUsername(@RequestParam String username) throws Exception {
        return userService.getUserByUsername(Role.ROOT, username);
    }

    @GetMapping("/roots/{uniqueName}")
    @PreAuthorize("hasAuthority('READ_ROOT')")
    public UserDto getRootByUniqueName(@PathVariable String uniqueName) throws Exception {
        return userService.getUserByUniqueName(Role.ROOT, uniqueName);
    }

    @GetMapping("/admins/uniqueNames")
    @PreAuthorize("hasAuthority('READ_ADMIN')")
    public Collection<UserDto> findAdminsDifferentFromTheCurrentUserByTheirUniqueNameStartingWithPrefix(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "5") int usersNumber
    ) throws Exception {
        return userService.findUsersDifferentFromTheCurrentUserByTheirUniqueNameStartingWithPrefix(prefix, usersNumber, Role.ADMIN);
    }
}
