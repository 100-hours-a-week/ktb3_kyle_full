package com.kyle.week4.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PostLikeCountMemoryCache implements PostLikeCountCache {
    private final ConcurrentHashMap<Long, AtomicInteger> postLikeCount = new ConcurrentHashMap<>();

    @Override
    public int count(Long postId) {
        return postLikeCount.get(postId).get();
    }

    @Override
    public int increase(Long postId) {
        return postLikeCount.computeIfAbsent(postId,
          k -> new AtomicInteger(0)
        ).incrementAndGet();
    }

    @Override
    public int decrease(Long postId) {
        return postLikeCount.get(postId).decrementAndGet();
    }

    @Override
    public void backUp(Long postId) {

    }
}
