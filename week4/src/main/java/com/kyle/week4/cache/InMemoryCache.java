package com.kyle.week4.cache;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InMemoryCache<T extends Map<Long, AtomicInteger>> implements CountCache {
    private final T countCache;

    @Override
    public void initCache(Long postId) {
        countCache.put(postId, new AtomicInteger(0));
    }

    @Override
    public int count(Long postId) {
        return countCache.computeIfAbsent(postId, key -> new AtomicInteger(0)).get();
    }

    @Override
    public Map<Long, Integer> getCounts(List<Long> postIds) {
        return postIds.stream().collect(
          Collectors.toMap(
            postId -> postId,
            postId -> count(postId)
          )
        );
    }

    @Override
    public int increase(Long postId) {
        return countCache.computeIfAbsent(postId,
          k -> new AtomicInteger(0)
        ).incrementAndGet();
    }

    @Override
    public int decrease(Long postId) {
        return countCache.get(postId).decrementAndGet();
    }

    @Override
    public void clear() {
        countCache.clear();
    }
}
