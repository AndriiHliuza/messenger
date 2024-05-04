package com.app.messenger.controller.dto;

import com.app.messenger.repository.model.AccountState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDto {
    private UserDto userDto;
    private AccountState state;
    private ZonedDateTime createdAt;
    private ZonedDateTime activatedAt;
}
