package com.kyle.week4.repository;

import com.kyle.week4.entity.PostLike;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostLikeRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentHashMap<Long, PostLike> database = new ConcurrentHashMap<>();

    public PostLike save(PostLike postLike) {
        if (postLike.isNew()) {
            Long postLikeId = primaryKey.getAndIncrement();
            postLike.assignId(postLikeId);
        }
        database.put(postLike.getId(), postLike);
        return postLike;
    }

    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return database.values().stream()
          .anyMatch(postLike -> postLike.isLiked(userId, postId));
    }

    public Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId) {
        return database.values().stream()
          .filter(postLike -> postLike.isLiked(userId, postId))
          .findFirst();
    }

    public void delete(PostLike postLike) {
        database.remove(postLike.getId());
    }

    public void clear() {
        primaryKey.set(1);
        database.clear();
    }
}
