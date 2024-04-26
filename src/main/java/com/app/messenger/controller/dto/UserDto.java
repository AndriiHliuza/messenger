package com.app.messenger.controller.dto;

import com.app.messenger.repository.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private UserImageDto userImage;
}
