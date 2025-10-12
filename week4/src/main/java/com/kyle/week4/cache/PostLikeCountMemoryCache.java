package com.kyle.week4.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PostLikeCountMemoryCache implements PostLikeCountCache {
    private final ConcurrentHashMap<Long, AtomicInteger> likeCount = new ConcurrentHashMap<>();

    @Override
    public void initCache(Long postId) {
        likeCount.put(postId, new AtomicInteger(0));
    }

    @Override
    public int count(Long postId) {
        return likeCount.get(postId).get();
    }

    @Override
    public int increase(Long postId) {
        return likeCount.computeIfAbsent(postId,
          k -> new AtomicInteger(0)
        ).incrementAndGet();
    }

    @Override
    public int decrease(Long postId) {
        return likeCount.get(postId).decrementAndGet();
    }

    @Override
    public void backUp(Long postId) {

    }

    @Override
    public void clear() {
        likeCount.clear();
    }
}
