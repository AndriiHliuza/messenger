package com.app.messenger.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private ZonedDateTime registrationDate;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Jwt> jwts;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL)
    private List<SubscriptionSubscriber> subscriptions;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<SubscriptionSubscriber> subscribers;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserImage userImage;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
