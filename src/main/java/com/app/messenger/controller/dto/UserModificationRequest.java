package com.app.messenger.controller.dto;

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
    private String currentPassword;

    private String password;
    private String uniqueName;
    private String firstname;
    private String lastname;
    private LocalDate birthday;
    private MultipartFile userImage;
}
