package com.app.messenger.controller.dto;

import com.app.messenger.repository.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String uniqueName;
    private ZonedDateTime registrationDate;
    private String firstname;
    private String lastname;
    private LocalDate birthday;
    private Role role;
}
