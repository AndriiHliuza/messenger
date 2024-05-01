package com.app.messenger.security.controller.dto;

import com.app.messenger.security.annotation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    @NotNull
    @Email
    private String username;

    @NotNull
    @Password
    private String password;
}
