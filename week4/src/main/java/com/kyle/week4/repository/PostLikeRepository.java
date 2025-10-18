package com.kyle.week4.repository;

import com.kyle.week4.entity.PostLike;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PostLikeRepository {
    private final ConcurrentHashMap<String, PostLike> database = new ConcurrentHashMap<>();

    public PostLike save(PostLike postLike) {
        database.put(postLike.getId(), postLike);
        return postLike;
    }

    public boolean existsByUserIdAndPostId(String key) {
        return database.containsKey(key);
    }

    public Optional<PostLike> findByUserIdAndPostId(String key) {
        return Optional.ofNullable(database.get(key));
    }

    public void delete(PostLike postLike) {
        database.remove(postLike.getId());
    }

    public void clear() {
        database.clear();
    }
}
