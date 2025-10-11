package com.kyle.week4.service;

import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import com.kyle.week4.controller.response.CommentResponse;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.CommentRepository;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kyle.week4.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private static final int COMMENT_PAGE_SIZE = 10;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostDetailResponse createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Post post = request.toEntity(user);
        Post savedPost = postRepository.save(post);

        return PostDetailResponse.of(savedPost, userId, 0);
    }

    public List<PostResponse> infiniteScroll(Long lastPostId, int limit) {
        List<Post> posts = (lastPostId == null) ?
          postRepository.findAllInfiniteScroll(limit) :
          postRepository.findAllInfiniteScroll(lastPostId, limit);

        return posts.stream()
          .map(post ->
            PostResponse.of(
              post,
              postRepository.getViewCount(post.getId()))
          )
          .toList();
    }

    public PostDetailResponse getPostDetail(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
          .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        int viewCount = postRepository.increaseViewCount(postId);

        return PostDetailResponse.of(post, userId, viewCount);
    }

    public PostDetailResponse updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
          .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        if (post.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }
        post.updatePost(request);

        int viewCount = postRepository.getViewCount(postId);

        return PostDetailResponse.of(post, userId, viewCount);
    }
}
