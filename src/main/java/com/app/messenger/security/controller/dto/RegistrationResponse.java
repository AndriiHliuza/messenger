package com.app.messenger.security.controller.dto;

import com.app.messenger.controller.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private UserDto user;
    private boolean isRegistrationSuccessful;
}
