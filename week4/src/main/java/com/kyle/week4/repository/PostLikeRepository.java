package com.kyle.week4.repository;

import com.kyle.week4.entity.PostLike;

import java.util.Optional;

public interface PostLikeRepository {
    PostLike save(PostLike postLike);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
    void delete(PostLike postLike);
    void clear();
}
