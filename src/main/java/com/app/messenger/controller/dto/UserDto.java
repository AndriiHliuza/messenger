package com.app.messenger.controller.dto;

import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.Status;
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
    private String registrationDate;
    private String firstname;
    private String lastname;
    private String birthday;
    private Role role;
    private Status status;
    private UserImageDto userImage;
}
