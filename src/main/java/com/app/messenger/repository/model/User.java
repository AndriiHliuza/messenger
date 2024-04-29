package com.app.messenger.repository.model;

import com.app.messenger.websocket.repository.model.ChatMember;
import com.app.messenger.websocket.repository.model.Message;
import com.app.messenger.websocket.repository.model.MessageStatus;
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

//    @Enumerated(EnumType.STRING)
//    private Status status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Jwt> jwts;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL)
    private List<SubscriptionSubscriber> subscriptions;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<SubscriptionSubscriber> subscribers;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserImage userImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatMember> chats;

    @OneToMany(mappedBy = "sender", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    private List<Message> messages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MessageStatus> messageStatuses;

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
