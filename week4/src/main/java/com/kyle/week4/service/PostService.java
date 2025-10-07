package com.kyle.week4.service;

import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.Post;
import com.kyle.week4.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponse createPost(Long userId, PostCreateRequest request) {
        Post post = request.toEntity(userId);
        Post savedPost = postRepository.save(post);

        return PostResponse.of(post, userId);
    }
}
