package com.app.messenger.service;

import com.app.messenger.controller.dto.PostDto;
import com.app.messenger.controller.dto.UserDto;
import com.app.messenger.exception.PostNotFoundException;
import com.app.messenger.exception.UserNotFoundException;
import com.app.messenger.repository.PostRepository;
import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.Post;
import com.app.messenger.repository.model.User;
import com.app.messenger.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostConverter postConverter;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    @Override
    public List<PostDto> getUserPosts(String username) throws Exception {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("user with username " + username + " not found")
                );

        List<Post> posts = postRepository.findAllByUserId(user.getId());
        List<PostDto> postsToReturn = new ArrayList<>();
        for (Post post : posts) {
            postsToReturn.add(postConverter.toDto(post));
        }
        return postsToReturn;
    }

    @Override
    public PostDto createPost(PostDto postDto) throws Exception {
        User currentUser = authenticationService.getCurrentUser();
        UserDto userDto = postDto.getUser();
        if (userDto == null || !currentUser.getUsername().equals(userDto.getUsername())) {
            throw new IllegalArgumentException("Invalid UserDto");
        }

        Post postToSave = postConverter.toEntity(postDto);
        Post savedPost = postRepository.save(postToSave);
        return postConverter.toDto(savedPost);
    }

    @Override
    public PostDto deletePost(String postId) throws Exception {
        User currentUser = authenticationService.getCurrentUser();

        UUID convertedPostId = UUID.fromString(postId.toUpperCase());
        Post postToDelete = postRepository
                .findById(convertedPostId)
                .orElseThrow(
                        () -> new PostNotFoundException("Post with id " + convertedPostId + " not found")
                );

        User postCreator = postToDelete.getUser();
        if (!currentUser.getUsername().equals(postCreator.getUsername())) {
            throw new UnsupportedOperationException("Can not delete post that does not belong to current user");
        }

        postRepository.deleteById(postToDelete.getId());
        return postConverter.toDto(postToDelete);
    }
}
