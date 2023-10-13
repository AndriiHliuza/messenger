package com.app.messenger.repository.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {

    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    Permission.READ_USER,
                    Permission.CREATE_USER,
                    Permission.UPDATE_USER,
                    Permission.DELETE_USER,
                    Permission.CREATE_ADMIN
            )
    ),
    ROOT(
            Set.of(
                    Permission.READ_USER,
                    Permission.CREATE_USER,
                    Permission.UPDATE_USER,
                    Permission.DELETE_USER,
                    Permission.READ_ADMIN,
                    Permission.CREATE_ADMIN,
                    Permission.UPDATE_ADMIN,
                    Permission.DELETE_ADMIN
            )
    );

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = permissions
                .stream()
                .map(
                        permission -> new SimpleGrantedAuthority(permission.name())
                )
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + name()));

        return authorities;
    }
}
