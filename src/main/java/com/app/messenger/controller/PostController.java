package com.app.messenger.controller;

import com.app.messenger.controller.dto.PostDto;
import com.app.messenger.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ResponseStatus(HttpStatus.OK)
public class PostController {
    private final PostService postService;

    @GetMapping("/users/{username}/posts")
    public Collection<PostDto> getUserPosts(@PathVariable String username) throws Exception {
        return postService.getUserPosts(username);
    }

    @PostMapping("/posts")
    public PostDto createPost(@RequestBody PostDto postDto) throws Exception {
        return postService.createPost(postDto);
    }

    @DeleteMapping("/posts/{postId}")
    public PostDto deletePost(
            @PathVariable String postId
    ) throws Exception {
        return postService.deletePost(postId);
    }
}
