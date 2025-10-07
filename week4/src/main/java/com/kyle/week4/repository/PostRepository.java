package com.kyle.week4.repository;

import com.kyle.week4.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentHashMap<Long, Post> database = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> viewCount = new ConcurrentHashMap<>();

    public Post save(Post post) {
        Long postId = primaryKey.getAndIncrement();
        post.assignId(postId);
        database.putIfAbsent(postId, post);
        return post;
    }

    public void clear() {
        primaryKey.set(1);
        database.clear();
        viewCount.clear();
    }
}
