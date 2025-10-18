package com.kyle.week4.cache;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public Map<Long, Integer> getCounts(List<Long> postIds) {
        return postIds.stream().collect(
          Collectors.toMap(
            postId -> postId,
            postId -> viewCount.get(postId).get()
          )
        );
    }

    @Override
    public int increase(Long postId) {
        return viewCount.computeIfAbsent(postId,
          k -> new AtomicInteger(0)
        ).incrementAndGet();
    }

    @Override
    public void clear() {
        viewCount.clear();
    }
}
