package com.app.messenger.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private String postId;
    private UserDto user;
    private String title;
    private String content;
    private ZonedDateTime createdAt;
}
