package com.app.messenger.config;

import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Value("${security.root.password}")
    private String ROOT_PASSWORD;

    @Value("${security.admin.password}")
    private String ADMIN_PASSWORD;

    private final UserRepository userRepository;

    @Bean
    @Transactional
    public CommandLineRunner run(PasswordEncoder passwordEncoder) {
        return args -> {

            if (!userRepository.existsByUsername("root@mail.com")) {

                User root = User
                        .builder()
                        .username("root@mail.com")
                        .password(passwordEncoder.encode(ROOT_PASSWORD))
                        .registrationDate(ZonedDateTime.now())
                        .firstname("root")
                        .lastname("root")
                        .role(Role.ROOT)
                        .build();

                userRepository.save(root);
            }

            if (!userRepository.existsByUsername("admin@mail.com")) {

                User admin = User
                        .builder()
                        .username("admin@mail.com")
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .registrationDate(ZonedDateTime.now())
                        .firstname("admin")
                        .lastname("admin")
                        .role(Role.ADMIN)
                        .build();

                userRepository.save(admin);
            }
        };
    }
}
