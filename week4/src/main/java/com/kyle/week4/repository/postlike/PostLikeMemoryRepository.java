package com.kyle.week4.repository.postlike;

import com.kyle.week4.entity.PostLike;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PostLikeMemoryRepository implements PostLikeRepository {
    private final ConcurrentHashMap<String, PostLike> database = new ConcurrentHashMap<>();

    @Override
    public PostLike save(PostLike postLike) {
        String key = generateKey(postLike.getUserId(), postLike.getPostId());
        database.put(key, postLike);
        return postLike;
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        String key = generateKey(userId, postId);
        return database.containsKey(key);
    }

    @Override
    public Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId) {
        String key = generateKey(userId, postId);
        return Optional.ofNullable(database.get(key));
    }

    @Override
    public void delete(PostLike postLike) {
        String key = generateKey(postLike.getUserId(), postLike.getPostId());
        database.remove(key);
    }

    @Override
    public void clear() {
        database.clear();
    }

    private String generateKey(Long userId, Long postId) {
        return userId + ":" + postId;
    }
}
