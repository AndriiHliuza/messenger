package com.app.messenger.controller;

import com.app.messenger.controller.dto.Subscription;
import com.app.messenger.controller.dto.UserAccountDto;
import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.controller.dto.UserImageDto;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.UserAccount;
import com.app.messenger.service.UserImageService;
import com.app.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ResponseStatus(HttpStatus.OK)
public class UserController {

    private final UserService userService;
    private final UserImageService userImageService;

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('READ_USER')")
    public UserDto getUserByUsername(@RequestParam String username) throws Exception {
        return userService.getUserByUsername(Role.USER, username);
    }

    @GetMapping("/users/{uniqueName}")
    @PreAuthorize("hasAuthority('READ_USER')")
    public UserDto getUserByUniqueName(@PathVariable String uniqueName) throws Exception {
        return userService.getUserByUniqueName(Role.USER, uniqueName);
    }

    @GetMapping("/users/{username}/account")
    @PreAuthorize("hasAuthority('READ_USER')")
    public UserAccountDto getUserAccountByUserUsername(@PathVariable String username) throws Exception {
        return userService.getUserAccountByUserUsername(username);
    }

    @PatchMapping("/users/{username}/account")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public UserAccountDto modifyUserAccount(
            @PathVariable String username,
            @RequestBody UserAccountDto userAccountDto
    ) throws Exception {
        return userService.modifyUserAccount(username, userAccountDto);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('READ_USER')")
    public Collection<UserDto> getUsersPagedAndSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ASC") String order
    ) throws Exception {
        return userService.getUsersPagedAndSorted(page, size, order, Role.USER);
    }

    @PatchMapping("/users/{username}")
    @PreAuthorize("hasAnyAuthority('UPDATE_USER', 'UPDATE_ADMIN', 'UPDATE_ROOT')")
    public UserDto updateUser(
            @PathVariable String username,
            @RequestParam String currentPassword,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String uniqueName,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) LocalDate birthday,
            @RequestParam(required = false) MultipartFile userImage
    ) throws Exception {
        return userService.updateUser(
                username,
                currentPassword,
                password,
                uniqueName,
                firstname,
                lastname,
                birthday,
                userImage
        );
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public UserDto deleteUser(@PathVariable String username) throws Exception {
        return userService.deleteUser(username);
    }

    @GetMapping("/users/{uniqueName}/image")
    @PreAuthorize("hasAuthority('READ_USER')")
    public ResponseEntity<byte[]> getUserImage(@PathVariable String uniqueName) throws Exception {
        UserImageDto userImageDto = userImageService.getUserImage(uniqueName);
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(userImageDto.getType()))
                .body(userImageDto.getData());
    }

    @GetMapping("/users/{uniqueName}/image/metadata")
    @PreAuthorize("hasAuthority('READ_USER')")
    public UserImageDto getUserImageMetadata(@PathVariable String uniqueName) throws Exception {
        return userImageService.getUserImage(uniqueName);
    }

    @GetMapping("/users/{uniqueName}/subscriptions")
    @PreAuthorize("hasAuthority('READ_USER')")
    public List<UserDto> getUserSubscriptions(@PathVariable String uniqueName) throws Exception {
        return userService.getUserSubscriptions(uniqueName);
    }

    @GetMapping("/users/{uniqueName}/subscribers")
    @PreAuthorize("hasAuthority('READ_USER')")
    public List<UserDto> getUserSubscribers(@PathVariable String uniqueName) throws Exception {
        return userService.getUserSubscribers(uniqueName);
    }

    @PostMapping("/users/{authenticatedUserUniqueName}/subscriptions")
    @PreAuthorize("hasRole('USER')")
    public Subscription subscribe(
            @PathVariable String authenticatedUserUniqueName,
            @RequestBody Subscription subscription
    ) throws Exception {
        return userService.subscribe(authenticatedUserUniqueName, subscription);
    }

    @GetMapping("/users/{userUniqueName}/subscriptions/{userSubscriptionUniqueName}")
    @PreAuthorize("hasRole('USER')")
    public Subscription isSubscribed(
            @PathVariable String userUniqueName,
            @PathVariable String userSubscriptionUniqueName
    ) throws Exception {
        return userService.isSubscribed(userUniqueName, userSubscriptionUniqueName);
    }

    @DeleteMapping("/users/{userUniqueName}/subscriptions/{userSubscriptionUniqueName}")
    @PreAuthorize("hasRole('USER')")
    public Subscription unsubscribe(
            @PathVariable String userUniqueName,
            @PathVariable String userSubscriptionUniqueName
    ) throws Exception {
        return userService.unsubscribe(userUniqueName, userSubscriptionUniqueName);
    }

    @GetMapping("/users/uniqueNames")
    @PreAuthorize("hasAuthority('READ_USER')")
    public Collection<UserDto> findUsersDifferentFromTheCurrentUserByTheirUniqueNameStartingWithPrefix(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "5") int usersNumber
    ) throws Exception {
        return userService.findUsersDifferentFromTheCurrentUserByTheirUniqueNameStartingWithPrefix(prefix, usersNumber, Role.USER);
    }
}
