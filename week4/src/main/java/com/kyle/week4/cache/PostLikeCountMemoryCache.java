package com.kyle.week4.cache;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public Map<Long, Integer> getCounts(List<Long> postIds) {
        return postIds.stream().collect(
          Collectors.toMap(
            postId -> postId,
            postId -> likeCount.get(postId).get()
          )
        );
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
    public void clear() {
        likeCount.clear();
    }
}
