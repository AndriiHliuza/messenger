package com.app.messenger.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserImageDto implements Serializable {
    private String name;
    private String type;
    private byte[] data;
    private String username;
}
