package com.app.messenger.security.config;

import com.app.messenger.repository.model.Role;
import com.app.messenger.security.service.JwtAuthenticationFilter;
import com.app.messenger.security.service.UserAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final LogoutHandler logoutHandler;
    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(
                        httpSecurityHttpBasicConfigurer ->
                                httpSecurityHttpBasicConfigurer.authenticationEntryPoint(userAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(
                        request -> {
                            request.requestMatchers(
                                            HttpMethod.POST,
                                            "/api/registration",
                                            "/api/authentication",
                                            "/api/jwt/**"
                                    ).permitAll()

                                    .requestMatchers(
                                            "/api/ws/**"
                                    ).permitAll()

                                    .requestMatchers(
                                            "/api/user/**",
                                            "/api/users/**"
                                    ).hasAnyRole(
                                            Role.USER.name(),
                                            Role.ADMIN.name(),
                                            Role.ROOT.name()
                                    )
                                    .requestMatchers(
                                            "/api/admin/**",
                                            "/api/admins/**"
                                    ).hasAnyRole(
                                            Role.ADMIN.name(),
                                            Role.ROOT.name()
                                    )
                                    .requestMatchers(
                                            "/api/root/**",
                                            "/api/roots/**"
                                    ).hasRole(Role.ROOT.name())

                                    .anyRequest().authenticated();

                        }
                )
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(
                        httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                                .logoutUrl("/api/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(
                                        (request, response, authentication) -> SecurityContextHolder.clearContext()
                                )

                )
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
