package com.app.messenger.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private AccountState state;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    private ZonedDateTime activatedAt;

    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private UserAccountActivationCode userAccountActivationCode;
}
