package com.app.messenger.config;

import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.Status;
import com.app.messenger.repository.model.User;
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

    @Value("${application.security.root.password}")
    private String ROOT_PASSWORD;

    @Value("${application.security.admin.password}")
    private String ADMIN_PASSWORD;

    private final UserRepository userRepository;

    @Bean
    @Transactional
    public CommandLineRunner run(PasswordEncoder passwordEncoder) {
        return args -> {

            if (!userRepository.existsByUsername("root@root")) {

                User root = User
                        .builder()
                        .username("root@root")
                        .password(passwordEncoder.encode(ROOT_PASSWORD))
                        .uniqueName("root")
                        .registrationDate(ZonedDateTime.now())
                        .firstname("root")
                        .lastname("root")
                        .role(Role.ROOT)
                        .status(Status.OFFLINE)
                        .build();

                userRepository.save(root);
            }

            if (!userRepository.existsByUsername("admin@admin")) {

                User admin = User
                        .builder()
                        .username("admin@admin")
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .uniqueName("admin")
                        .registrationDate(ZonedDateTime.now())
                        .firstname("admin")
                        .lastname("admin")
                        .role(Role.ADMIN)
                        .status(Status.OFFLINE)
                        .build();

                userRepository.save(admin);
            }
        };
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
}
