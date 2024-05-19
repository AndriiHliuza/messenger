package com.app.messenger.service;

import com.app.messenger.controller.dto.PostDto;

import java.util.List;

public interface PostService {
    List<PostDto> getUserPosts(String username) throws Exception;
    PostDto createPost(PostDto postDto) throws Exception;
    PostDto deletePost(String postId) throws Exception;
}
