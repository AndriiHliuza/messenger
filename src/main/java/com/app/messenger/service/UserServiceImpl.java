package com.app.messenger.service;

import com.app.messenger.controller.dto.Subscription;
import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.controller.dto.UserModificationRequest;
import com.app.messenger.exception.SubscriptionSubscriberAlreadyExistsException;
import com.app.messenger.exception.SubscriptionSubscriberNotExists;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.SubscriptionSubscriberRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.SubscriptionSubscriber;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.exception.PasswordNotValidException;
import com.app.messenger.security.exception.UserNotAuthenticatedException;
import com.app.messenger.security.service.AuthenticationService;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SubscriptionSubscriberRepository subscriptionSubscriberRepository;

    private final UserConverter userConverter;
    private final SubscriptionSubscriberConverter subscriptionSubscriberConverter;

    private final UserModifier userModifier;

    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUserByUsername(Role role, String username) throws Exception {
        User user = userRepository
                .findByRoleAndUsername(role, username)
                .orElseThrow(
                        () -> new UserNotFoundException("User with uniqueName " + username + " not found")
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
    public Collection<UserDto> getUsersPagedAndSorted(int page, int size, String order) throws Exception {
        Sort.Direction sortOrder = Sort.Direction.valueOf(order.toUpperCase());
        Sort sort = Sort.by(sortOrder, "uniqueName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Collection<User> users = getUsersPagedAndSortedForCurrentAuthenticatedUser(pageable).getContent();
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

    private Page<User> getUsersPagedAndSortedForCurrentAuthenticatedUser(Pageable pageable) throws UserNotAuthenticatedException {
        UserDetails currentAuthenticatedUser = authenticationService.getAuthenticatedUserFromSecurityContext();
        if (currentAuthenticatedUser == null) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        String usernameToExclude = currentAuthenticatedUser.getUsername();

        return userRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("role"), Role.USER));
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
            throw new SubscriptionSubscriberNotExists("User with uniqueName " + userUniqueName + "is not subscribed on user with uniqueName " + userSubscriptionUniqueName);
        } else {
            subscriptionSubscriberRepository.delete(storedSubscriptionSubscriber);
        }

        return subscriptionSubscriberConverter.toDto(storedSubscriptionSubscriber);
    }
}
