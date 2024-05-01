package com.app.messenger.controller.dto;

import com.app.messenger.security.annotation.Password;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModificationRequest {

    @NotNull
    @Password
    private String currentPassword;
    private String password;
    private String uniqueName;
    private String firstname;
    private String lastname;

    @Past
    private LocalDate birthday;
    private MultipartFile userImage;
}
