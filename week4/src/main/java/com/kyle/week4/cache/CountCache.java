package com.kyle.week4.cache;

import java.util.List;
import java.util.Map;

public interface CountCache {
    void initCache(Long postId);

    int count(Long postId);

    Map<Long, Integer> getCounts(List<Long> postIds);

    int increase(Long postId);

    int decrease(Long postId);

    void clear();
}
