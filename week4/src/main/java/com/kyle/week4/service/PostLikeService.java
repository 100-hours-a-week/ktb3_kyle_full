package com.kyle.week4.service;

import com.kyle.week4.cache.PostLikeCountCache;
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
    private final PostLikeCountCache postLikeCountCache;

    public PostLikeResponse count(Long userId, Long postId) {
        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        int likeCount = postLikeCountCache.count(postId);
        return new PostLikeResponse(likeCount, isLiked);
    }

    public PostLikeResponse like(Long userId, Long postId) {
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new CustomException(ALREADY_LIKED_ERROR);
        }

        if (postRepository.notExistsById(postId)) {
            throw new CustomException(POST_NOT_FOUND);
        }

        PostLike postLike = new PostLike(userId, postId);
        postLikeRepository.save(postLike);

        int likeCount = postLikeCountCache.increase(postId);

        return new PostLikeResponse(likeCount, true);
    }

    public PostLikeResponse removeLike(Long userId, Long postId) {
        if (postRepository.notExistsById(postId)) {
            throw new CustomException(POST_NOT_FOUND);
        }

        PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId)
          .orElseThrow(() -> new CustomException(POST_LIKE_NOT_FOUND));
        postLikeRepository.delete(postLike);

        int likeCount = postLikeCountCache.decrease(postId);

        return new PostLikeResponse(likeCount, false);
    }
}
