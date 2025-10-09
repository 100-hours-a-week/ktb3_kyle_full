package com.kyle.week4.service;

import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kyle.week4.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostDetailResponse createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Post post = request.toEntity(userId);
        Post savedPost = postRepository.save(post);

        return PostDetailResponse.of(savedPost, user);
    }

    public List<PostResponse> infiniteScroll(Long lastPostId, int limit) {
        List<Post> posts = (lastPostId == null) ?
          postRepository.findAllInfiniteScroll(limit) :
          postRepository.findAllInfiniteScroll(lastPostId, limit);

        return posts.stream()
          .map(post -> {
              User user = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
              return PostResponse.of(post, user);
          })
          .toList();
    }
}
