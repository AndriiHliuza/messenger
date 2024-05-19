package com.app.messenger.service;

import com.app.messenger.controller.dto.Subscription;
import com.app.messenger.controller.dto.UserAccountDto;
import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.controller.dto.UserModificationRequest;
import com.app.messenger.exception.SubscriptionSubscriberAlreadyExistsException;
import com.app.messenger.exception.SubscriptionSubscriberNotExistsException;
import com.app.messenger.exception.UserAccountNotFoundException;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.SubscriptionSubscriberRepository;
import com.app.messenger.repository.UserAccountRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.*;
import com.app.messenger.security.controller.dto.RegistrationRequest;
import com.app.messenger.security.controller.dto.RegistrationResponse;
import com.app.messenger.security.exception.PasswordNotValidException;
import com.app.messenger.security.exception.UserAlreadyExistsException;
import com.app.messenger.security.exception.UserDeletionException;
import com.app.messenger.security.exception.UserNotAuthenticatedException;
import com.app.messenger.security.service.AuthenticationService;
import com.app.messenger.websocket.service.MessageService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final SubscriptionSubscriberRepository subscriptionSubscriberRepository;

    private final UserConverter userConverter;
    private final UserAccountConverter userAccountConverter;
    private final UserRegistrationConverter userRegistrationConverter;
    private final SubscriptionSubscriberConverter subscriptionSubscriberConverter;

    private final UserModifier userModifier;

    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    private final MessageService messageService;

    @Override
    public UserDto getUserByUsername(Role role, String username) throws Exception {
        User user = userRepository
                .findByRoleAndUsername(role, username)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + username + " not found")
                );
        return userConverter.toDto(user);
    }

    @Override
    public UserDto getUserByUniqueName(Role role, String uniqueName) throws Exception {
        User user = userRepository
                .findByRoleAndUniqueName(role, uniqueName)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + uniqueName + " not found")
                );
        return userConverter.toDto(user);
    }

    @Override
    public UserAccountDto getUserAccountByUserUsername(String username) throws Exception {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + username + " not found")
                );
        UserAccount userAccount = user.getUserAccount();

        return userAccountConverter.toDto(userAccount);
    }

    @Override
    public UserAccountDto modifyUserAccount(String username, UserAccountDto userAccountDto) throws Exception {
        if (username == null || userAccountDto == null) {
            throw new IllegalArgumentException("UserAccountDto or username is null");
        }

        UserDto userDto = userAccountDto.getUserDto();
        if (userDto == null || !username.equals(userDto.getUsername())) {
            throw new IllegalArgumentException("Usernames are different");
        }

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("user with username " + username + " not found")
                );

        UserAccount userAccount = userAccountRepository
                .findByUserUsername(username)
                .orElseThrow(
                        () -> new UserAccountNotFoundException("User account for user with username "
                                + user.getUsername() + " not found")
                );

        AccountState accountState = userAccountDto.getState();
        if (!userAccount.getState().equals(accountState)) {
            userAccount.setState(accountState);
        }
        UserAccount savedUserAccount = userAccountRepository.save(userAccount);

        return userAccountConverter.toDto(savedUserAccount);
    }

    @Override
    public Collection<UserDto> getUsersPagedAndSorted(int page, int size, String order, Role userRole) throws Exception {
        Sort.Direction sortOrder = Sort.Direction.valueOf(order.toUpperCase());
        Sort sort = Sort.by(sortOrder, "uniqueName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Collection<User> users = getUsersPagedAndSortedForCurrentUser(pageable, userRole).getContent();
        List<UserDto> usersToReturn = new ArrayList<>();
        for (User user : users) {
            usersToReturn.add(userConverter.toDto(user));
        }
        return usersToReturn;
    }

    @Override
    public UserDto updateUser(
            String username,
            String currentPassword,
            String password,
            String uniqueName,
            String firstname,
            String lastname,
            LocalDate birthday,
            MultipartFile userImage
    ) throws Exception {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + username + " not found")
                );

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordNotValidException("Passwords do not match for user with username " + username);
        }
        UserModificationRequest userModificationRequest = UserModificationRequest
                .builder()
                .currentPassword(currentPassword)
                .password(password)
                .uniqueName(uniqueName)
                .firstname(firstname)
                .lastname(lastname)
                .birthday(birthday)
                .userImage(userImage)
                .build();

        user = userModifier.modify(userModificationRequest, user);
        user = userRepository.save(user);
        return userConverter.toDto(user);
    }

    @Override
    public UserDto deleteUser(String username) throws Exception {
        User userToDelete = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + username + " not found")
                );

        validateUserDeletion(userToDelete);

        UserDto userToDeleteDto = userConverter.toDto(userToDelete);
        messageService.sendMessageToChatOnUserDeletion(userToDeleteDto);
        userRepository.delete(userToDelete);

        return userToDeleteDto;
    }

    @Override
    public List<UserDto> getUserSubscriptions(String uniqueName) throws Exception {
        User user = userRepository
                .findByUniqueName(uniqueName)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + uniqueName + " not found")
                );
        List<SubscriptionSubscriber> subscriptionSubscribers = subscriptionSubscriberRepository.findBySubscriberId(user.getId());
        return convertUserStreamToUserDtoList(subscriptionSubscribers
                .stream()
                .map(SubscriptionSubscriber::getSubscription));
    }

    @Override
    public List<UserDto> getUserSubscribers(String uniqueName) throws Exception {
        User user = userRepository
                .findByUniqueName(uniqueName)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + uniqueName + " not found")
                );
        List<SubscriptionSubscriber> subscriptionSubscribers = subscriptionSubscriberRepository.findBySubscriptionId(user.getId());
        return convertUserStreamToUserDtoList(subscriptionSubscribers
                .stream()
                .map(SubscriptionSubscriber::getSubscriber));
    }

    private List<UserDto> convertUserStreamToUserDtoList(Stream<User> userStream) throws Exception {
        List<User> users = userStream.toList();
        List<UserDto> usersToReturn = new ArrayList<>();
        for (User user : users) {
            usersToReturn.add(userConverter.toDto(user));
        }
        return usersToReturn;
    }

    private Page<User> getUsersPagedAndSortedForCurrentUser(Pageable pageable, Role userRole) throws UserNotAuthenticatedException {
        UserDetails currentUser = authenticationService.getAuthenticatedUserUserDetailsFromSecurityContext();
        if (currentUser == null) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        String usernameToExclude = currentUser.getUsername();

        return userRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("role"), userRole));
            predicates.add(criteriaBuilder.notEqual(root.get("username"), usernameToExclude));
            return query.where(predicates.toArray(Predicate[]::new)).getRestriction();
        }, pageable);
    }

    @Override
    public Subscription subscribe(String subscriberUniqueName, Subscription subscription) throws Exception {

        if (!subscriberUniqueName.equals(subscription.getSubscriberUniqueName())) {
            throw new IllegalArgumentException("Unique names of subscriber does not match");
        }

        SubscriptionSubscriber subscriptionSubscriber = subscriptionSubscriberConverter.toEntity(subscription);
        if (subscriptionSubscriberRepository.existsBySubscriptionAndSubscriber(
                subscriptionSubscriber.getSubscription(),
                subscriptionSubscriber.getSubscriber()
        )) {
            throw new SubscriptionSubscriberAlreadyExistsException("Subscription already exists");
        }

        SubscriptionSubscriber savedSubscriptionSubscriber = subscriptionSubscriberRepository.save(subscriptionSubscriber);
        return subscriptionSubscriberConverter.toDto(savedSubscriptionSubscriber);
    }

    @Override
    public Subscription isSubscribed(String userUniqueName, String userSubscriptionUniqueName) throws Exception {
        Subscription subscription = Subscription
                .builder()
                .subscriptionUniqueName(userSubscriptionUniqueName)
                .subscriberUniqueName(userUniqueName)
                .build();

        SubscriptionSubscriber subscriptionSubscriber = subscriptionSubscriberConverter.toEntity(subscription);
        return subscriptionSubscriberConverter.toDto(subscriptionSubscriber);
    }

    @Override
    public Subscription unsubscribe(String userUniqueName, String userSubscriptionUniqueName) throws Exception {
        Subscription subscriptionDto = Subscription
                .builder()
                .subscriptionUniqueName(userSubscriptionUniqueName)
                .subscriberUniqueName(userUniqueName)
                .build();
        SubscriptionSubscriber subscriptionSubscriber = subscriptionSubscriberConverter.toEntity(subscriptionDto);
        User subscription = subscriptionSubscriber.getSubscription();
        User subscriber = subscriptionSubscriber.getSubscriber();

        SubscriptionSubscriber storedSubscriptionSubscriber = subscriptionSubscriberRepository.findBySubscriptionAndSubscriber(
                subscription,
                subscriber
        );

        if (storedSubscriptionSubscriber == null) {
            throw new SubscriptionSubscriberNotExistsException("User with uniqueName " + userUniqueName + "is not subscribed on user with uniqueName " + userSubscriptionUniqueName);
        } else {
            subscriptionSubscriberRepository.delete(storedSubscriptionSubscriber);
        }

        return subscriptionSubscriberConverter.toDto(storedSubscriptionSubscriber);
    }

    @Override
    public Collection<UserDto> findUsersDifferentFromTheCurrentUserByTheirUniqueNameStartingWithPrefix(String prefix, int usersNumber, Role role) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        List<User> users = userRepository.findAllByUniqueNameStartingWithAndRole(prefix, role);
        List<UserDto> usersToReturn = new ArrayList<>();

        int numberUsersToReturn = Math.min(users.size(), usersNumber);

        for (int i = 0; i < numberUsersToReturn; i++) {
            User user = users.get(i);
            if (!currentUser.getUsername().equals(user.getUsername())) {
                usersToReturn.add(userConverter.toDto(user));
            }
        }

        return usersToReturn;
    }

    @Override
    public RegistrationResponse createAdmin(RegistrationRequest registrationRequest) throws Exception {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + registrationRequest.getUsername() + " already exists in database");
        } else if (userRepository.existsByUniqueName(registrationRequest.getUniqueName())) {
            throw new UserAlreadyExistsException("User with uniqueName " + registrationRequest.getUniqueName() + " already exists in database");
        }

        User userToSave = userRegistrationConverter.toEntity(registrationRequest);
        userToSave.setRole(Role.ADMIN);
        userToSave.setUserAccount(UserAccount
                .builder()
                .user(userToSave)
                .state(AccountState.ACTIVATED)
                .createdAt(ZonedDateTime.now())
                .activatedAt(ZonedDateTime.now())
                .build());
        User savedUser = userRepository.save(userToSave);

        UserDto registeredUserDto = userConverter.toDto(savedUser);

        return RegistrationResponse
                .builder()
                .user(registeredUserDto)
                .isRegistrationSuccessful(true)
                .build();
    }

    private void validateUserDeletion(User userToDelete) throws Exception {
        User currentUser = authenticationService.getCurrentUser();

        Role currentUserRole = currentUser.getRole();
        Role userToDeleteRole = userToDelete.getRole();

        if (userToDeleteRole.equals(Role.ROOT)) {
            throw new UserDeletionException("ROOT user can not be deleted");
        }

        if (!userToDelete.getUsername().equals(currentUser.getUsername())
                && !((currentUserRole.equals(Role.ADMIN) || currentUserRole.equals(Role.ROOT)) && userToDeleteRole.equals(Role.USER))
                && !(currentUserRole.equals(Role.ROOT) && userToDeleteRole.equals(Role.ADMIN))) {
            throw new UserDeletionException("User with username " + currentUser.getUsername()
                    + " has no rights to delete user with username " + userToDelete.getUsername());
        }
    }
}
