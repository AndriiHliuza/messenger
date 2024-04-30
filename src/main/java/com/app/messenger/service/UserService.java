package com.app.messenger.service;

import com.app.messenger.controller.dto.Subscription;
import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.repository.model.Role;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.security.controller.dto.RegistrationResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto getUserByUsername(Role role, String username) throws Exception;
    UserDto getUserByUniqueName(Role role, String uniqueName) throws Exception;
    Collection<UserDto> getUsersPagedAndSorted(int page, int size, String order, Role userRole) throws Exception;
    UserDto updateUser(
            String username,
            String currentPassword,
            String password,
            String uniqueName,
            String firstname,
            String lastname,
            LocalDate birthday,
            MultipartFile userImage
    ) throws Exception;
    UserDto deleteUser(String username) throws Exception;
    List<UserDto> getUserSubscriptions(String username) throws Exception;
    List<UserDto> getUserSubscribers(String username) throws Exception;
    Subscription subscribe(String subscriberUniqueName, Subscription subscription) throws Exception;
    Subscription isSubscribed(String userUniqueName, String userSubscriptionUniqueName) throws Exception;
    Subscription unsubscribe(String userUniqueName, String userSubscriptionUniqueName) throws Exception;
    Collection<UserDto> findUsersDifferentFromTheCurrentUserByTheirUniqueNameStartingWithPrefix(String prefix, int usersNumber, Role role) throws Exception;
    RegistrationResponse createAdmin(RegistrationRequest registrationRequest) throws Exception;
}
