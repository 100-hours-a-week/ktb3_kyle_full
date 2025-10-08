package com.kyle.week4.repository;

import com.kyle.week4.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentSkipListMap<Long, Post> database = new ConcurrentSkipListMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> viewCount = new ConcurrentHashMap<>();

    public Post save(Post post) {
        if (post.isNew()) {
            Long postId = primaryKey.getAndIncrement();
            post.assignId(postId);
        }
        database.put(post.getId(), post);
        return post;
    }

    public List<Post> findAllInfiniteScroll(int limit) {
        return database.descendingMap().values().stream()
          .limit(limit)
          .toList();
    }

    public List<Post> findAllInfiniteScroll(Long lastPostId, int limit) {
        return database.headMap(lastPostId, false).descendingMap().values().stream()
          .limit(limit)
          .toList();
    }

    public void clear() {
        primaryKey.set(1);
        database.clear();
        viewCount.clear();
    }
}
