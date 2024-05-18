package com.app.messenger.security.service;

import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.email.dto.EmailDto;
import com.app.messenger.email.dto.EmailTemplate;
import com.app.messenger.email.service.EmailService;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserAccountActivationCodeRepository;
import com.app.messenger.repository.model.*;
import com.app.messenger.security.controller.dto.*;
import com.app.messenger.security.exception.UserAccountBlockedException;
import com.app.messenger.security.exception.UserAccountNotActivatedException;
import com.app.messenger.security.exception.UserAlreadyExistsException;
import com.app.messenger.repository.JwtRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.security.exception.UserNotAuthenticatedException;
import com.app.messenger.service.UserConverter;
import com.app.messenger.service.UserRegistrationConverter;
import com.app.messenger.service.scheduler.AccountActivationCodeDeletionScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserAccountActivationCodeRepository userAccountActivationCodeRepository;
    private final JwtRepository jwtRepository;
    private final UserRegistrationConverter userRegistrationConverter;
    private final UserConverter userConverter;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CodeGenerator codeGenerator;
    private final EncryptionService encryptionServiceImpl;
    private final EmailService emailService;
    private final AccountActivationCodeDeletionScheduler accountActivationCodeDeletionScheduler;

    @Override
    public RegistrationResponse register(RegistrationRequest registrationRequest) throws Exception {
        if (userRepository.existsByUsernameAndUserAccountState(registrationRequest.getUsername(), AccountState.ACTIVATED)) {
            throw new UserAlreadyExistsException("User with username " + registrationRequest.getUsername() + " already exists in database");
        } else if (userRepository.existsByUniqueNameAndUserAccountState(registrationRequest.getUniqueName(), AccountState.ACTIVATED)) {
            throw new UserAlreadyExistsException("User with uniqueName " + registrationRequest.getUniqueName() + " already exists in database");
        }

        deleteNotActivatedUser(registrationRequest.getUsername());

        User userToSave = userRegistrationConverter.toEntity(registrationRequest);
        UserAccount userAccount = UserAccount
                .builder()
                .user(userToSave)
                .state(AccountState.REQUIRE_ACTIVATION)
                .createdAt(ZonedDateTime.now())
                .build();
        String activationCode = codeGenerator.generate(6);
        UserAccountActivationCode userAccountActivationCode = UserAccountActivationCode
                .builder()
                .userAccount(userAccount)
                .code(encryptionServiceImpl.encrypt(activationCode))
                .build();
        userAccount.setUserAccountActivationCode(userAccountActivationCode);
        userToSave.setUserAccount(userAccount);
        User savedUser = userRepository.save(userToSave);
        log.debug("New user with username: {} was saved to database", savedUser.getUsername());
        UserDto registeredUserDto = userConverter.toDto(savedUser);

        accountActivationCodeDeletionScheduler.scheduleUserDeletionForNotActivatedUser(savedUser);

        emailService.sendEmailForAccountActivation(
                registeredUserDto.getUsername(),
                registeredUserDto.getUniqueName(),
                activationCode
        );

        return RegistrationResponse
                .builder()
                .user(registeredUserDto)
                .isRegistrationSuccessful(true)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        User user = userRepository
                .findByUsername(authenticationRequest.getUsername())
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "User with username: "
                                        + authenticationRequest.getUsername() +
                                        "was not found in database"
                        )
                );

        validateUserAccount(user);

        return buildAuthenticationResponse(user);
    }

    @Override
    public EmailDto sendEmailForUserAccountActivation(String username, EmailDto emailDto) throws Exception {
        if (username == null || !username.equals(emailDto.getTo())) {
            throw new IllegalArgumentException("Usernames are different");
        }

        User user = userRepository
                .findByUsernameAndUserAccountState(emailDto.getTo(), AccountState.REQUIRE_ACTIVATION)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + emailDto.getTo() + " not found")
                );

        String activationCode = codeGenerator.generate(6);
        UserAccountActivationCode userAccountActivationCode = user.getUserAccount().getUserAccountActivationCode();
        userAccountActivationCode.setCode(encryptionServiceImpl.encrypt(activationCode));
        User savedUser = userRepository.save(user);

        emailService.sendEmailForAccountActivation(
                savedUser.getUsername(),
                savedUser.getUniqueName(),
                activationCode
        );

        return emailService.buildEmail(
                savedUser.getUsername(),
                EmailTemplate.ACCOUNT_ACTIVATION.getDescription(),
                EmailTemplate.ACCOUNT_ACTIVATION.getDescription()
        );
    }

    @Override
    public UserDto activateUserAccount(String username, UserAccountActivationRequest userAccountActivationRequest) throws Exception {
        String userAccountActivationRequestUsername = userAccountActivationRequest.getUsername();

        if (username == null || !username.equals(userAccountActivationRequestUsername)) {
            throw new IllegalArgumentException("Usernames are different");
        }

        String activationCode = userAccountActivationRequest.getActivationCode();

        User user = userRepository
                .findByUsernameAndUserAccountState(username, AccountState.REQUIRE_ACTIVATION)
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + username + " not found")
                );

        if (activationCode == null) {
            throw new IllegalArgumentException("Activation code is null");
        }

        User savedUser = null;

        UserAccount userAccount = user.getUserAccount();
        UserAccountActivationCode userAccountActivationCode = userAccount.getUserAccountActivationCode();

        String encryptedActivationCode = userAccountActivationCode.getCode();
        if (encryptionServiceImpl.matches(activationCode, encryptedActivationCode)) {
            userAccount.setState(AccountState.ACTIVATED);
            userAccount.setActivatedAt(ZonedDateTime.now());
            userAccount.setUserAccountActivationCode(null);

            savedUser = userRepository.save(user);

            userAccountActivationCodeRepository.deleteById(userAccountActivationCode.getId());
        }

        return userConverter.toDto(savedUser);
    }

    private AuthenticationResponse buildAuthenticationResponse(User user) throws Exception {
        Jwt accessToken = jwtUtil.generateToken(user, TokenTargetType.ACCESS);
        Jwt refreshToken = jwtUtil.generateToken(user, TokenTargetType.REFRESH);

        jwtRepository.save(accessToken);
        log.debug("New ACCESS TOKEN for user with username: {} was saved to database", user.getUsername());
        jwtRepository.save(refreshToken);
        log.debug("New REFRESH TOKEN for user with username: {} was saved to database", user.getUsername());

        return AuthenticationResponse
                .builder()
                .accessToken(accessToken.getPlainContent())
                .refreshToken(refreshToken.getPlainContent())
                .build();
    }

    @Override
    public UserDetails getAuthenticatedUserUserDetailsFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = null;
        if (authentication != null) {
            authenticatedUser = (UserDetails) authentication.getPrincipal();
        }
        return authenticatedUser;
    }

    @Override
    public User getCurrentUser() throws UserNotAuthenticatedException {
        UserDetails currentUserUserDetails = getAuthenticatedUserUserDetailsFromSecurityContext();
        if (currentUserUserDetails == null) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        return (User) currentUserUserDetails;
    }

    private void deleteNotActivatedUser(String username) {
        userRepository.findByUsernameAndUserAccountState(
                username,
                AccountState.REQUIRE_ACTIVATION
        ).ifPresent(userRepository::delete);
    }

    private void validateUserAccount(User user) throws Exception {
        AccountState userAccountState = user.getUserAccount().getState();
        if (userAccountState.equals(AccountState.REQUIRE_ACTIVATION)) {
            throw new UserAccountNotActivatedException("User account for user with username "
                    + user.getUsername() + " is not activated");
        }

        if (userAccountState.equals(AccountState.BLOCKED)) {
            throw new UserAccountBlockedException("User account for user with username "
                    + user.getUsername() + " is blocked");
        }
    }
}
