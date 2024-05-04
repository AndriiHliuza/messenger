package com.app.messenger.config;

import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.AccountState;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.User;
import com.app.messenger.repository.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Value("${application.security.root.username}")
    private String ROOT_USERNAME;
    @Value("${application.security.root.password}")
    private String ROOT_PASSWORD;
    @Value("${application.security.root.uniqueName}")
    private String ROOT_UNIQUE_NAME;
    @Value("${application.security.root.firstname}")
    private String ROOT_FIRST_NAME;
    @Value("${application.security.root.lastname}")
    private String ROOT_LAST_NAME;

    @Value("${application.security.admin.username}")
    private String ADMIN_USERNAME;
    @Value("${application.security.admin.password}")
    private String ADMIN_PASSWORD;
    @Value("${application.security.admin.uniqueName}")
    private String ADMIN_UNIQUE_NAME;
    @Value("${application.security.admin.firstname}")
    private String ADMIN_FIRST_NAME;
    @Value("${application.security.admin.lastname}")
    private String ADMIN_LAST_NAME;

    private final UserRepository userRepository;

    @Bean
    @Transactional
    public CommandLineRunner run(PasswordEncoder passwordEncoder) {
        return args -> {

            if (!userRepository.existsByUsername(ROOT_USERNAME)) {

                User root = User
                        .builder()
                        .username(ROOT_USERNAME)
                        .password(passwordEncoder.encode(ROOT_PASSWORD))
                        .uniqueName(ROOT_UNIQUE_NAME)
                        .registrationDate(ZonedDateTime.now())
                        .firstname(ROOT_FIRST_NAME)
                        .lastname(ROOT_LAST_NAME)
                        .role(Role.ROOT)
                        .build();

                UserAccount rootAccount = UserAccount
                        .builder()
                        .user(root)
                        .state(AccountState.ACTIVATED)
                        .createdAt(ZonedDateTime.now())
                        .activatedAt(ZonedDateTime.now())
                        .build();

                root.setUserAccount(rootAccount);

                userRepository.save(root);
            }

            if (!userRepository.existsByUsername(ADMIN_USERNAME)) {

                User admin = User
                        .builder()
                        .username(ADMIN_USERNAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .uniqueName(ADMIN_UNIQUE_NAME)
                        .registrationDate(ZonedDateTime.now())
                        .firstname(ADMIN_FIRST_NAME)
                        .lastname(ADMIN_LAST_NAME)
                        .role(Role.ADMIN)
                        .build();

                UserAccount adminAccount = UserAccount
                        .builder()
                        .user(admin)
                        .state(AccountState.ACTIVATED)
                        .createdAt(ZonedDateTime.now())
                        .activatedAt(ZonedDateTime.now())
                        .build();
                admin.setUserAccount(adminAccount);

                userRepository.save(admin);
            }
        };
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
}
