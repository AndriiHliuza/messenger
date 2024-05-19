package com.app.messenger.service;

import com.app.messenger.controller.dto.PostDto;
import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Post;
import com.app.messenger.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class PostConverter implements Converter<PostDto, Post> {
    private final UserConverter userConverter;
    private final UserRepository userRepository;
    @Override
    public PostDto toDto(Post post) throws Exception {
        UserDto userDto = userConverter.toDto(post.getUser());
        return PostDto
                .builder()
                .postId(post.getId().toString())
                .user(userDto)
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Override
    public Post toEntity(PostDto postDto) throws Exception {
        UserDto userDto = postDto.getUser();
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto is null");
        }

        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new IllegalArgumentException("Post content is empty");
        }

        if (postDto.getTitle() == null || postDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Post title is empty");
        }

        User user = userRepository
                .findByUsername(userDto.getUsername())
                .orElseThrow(
                        () -> new UserNotFoundException("User with username " + userDto.getUsername() + " not found")
                );
        return Post
                .builder()
                .user(user)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .createdAt(ZonedDateTime.now())
                .build();
    }
}
