package com.app.messenger.security.controller.dto;

import com.app.messenger.security.annotation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @Email
    @NotNull
    private String username;

    @NotNull
    @Password
    private String password;

    @NotNull
    private String uniqueName;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @Past
    private String birthday;
}
