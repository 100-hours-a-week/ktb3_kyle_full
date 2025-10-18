package com.kyle.week4.service;

import com.kyle.week4.cache.CountCache;
import com.kyle.week4.controller.response.PostLikeResponse;
import com.kyle.week4.entity.PostLike;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.PostLikeRepository;
import com.kyle.week4.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kyle.week4.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CountCache postLikeCountCache;

    public PostLikeResponse count(Long userId, Long postId) {
        String key = generateKey(userId, postId);
        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(key);
        int likeCount = postLikeCountCache.count(postId);
        return new PostLikeResponse(likeCount, isLiked);
    }

    public PostLikeResponse like(Long userId, Long postId) {
        String key = generateKey(userId, postId);

        if (postLikeRepository.existsByUserIdAndPostId(key)) {
            throw new CustomException(ALREADY_LIKED_ERROR);
        }

        if (postRepository.notExistsById(postId)) {
            throw new CustomException(POST_NOT_FOUND);
        }

        PostLike postLike = new PostLike(key);
        postLikeRepository.save(postLike);

        int likeCount = postLikeCountCache.increase(postId);

        return new PostLikeResponse(likeCount, true);
    }

    public PostLikeResponse removeLike(Long userId, Long postId) {
        if (postRepository.notExistsById(postId)) {
            throw new CustomException(POST_NOT_FOUND);
        }

        String key = generateKey(userId, postId);
        PostLike postLike = postLikeRepository.findByUserIdAndPostId(key)
          .orElseThrow(() -> new CustomException(POST_LIKE_NOT_FOUND));
        postLikeRepository.delete(postLike);

        int likeCount = postLikeCountCache.decrease(postId);

        return new PostLikeResponse(likeCount, false);
    }

    private String generateKey(Long userId, Long postId) {
        return userId + ":" + postId;
    }
}
