package com.kyle.week4.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PostViewCountMemoryCache implements PostViewCountCache {
    private final ConcurrentHashMap<Long, AtomicInteger> viewCount = new ConcurrentHashMap<>();

    @Override
    public void initCache(Long postId) {
        viewCount.put(postId, new AtomicInteger(0));
    }

    @Override
    public int count(Long postId) {
        return viewCount.get(postId).get();
    }

    @Override
    public int increase(Long postId) {
        return viewCount.computeIfAbsent(postId,
          k -> new AtomicInteger(0)
        ).incrementAndGet();
    }

    @Override
    public void backUp(Long postId) {

    }

    @Override
    public void clear() {
        viewCount.clear();
    }
}
