package com.kyle.week4.cache;

import java.util.List;
import java.util.Map;

public interface PostViewCountCache {
    void initCache(Long postId);

    int count(Long postId);

    Map<Long, Integer> getCounts(List<Long> postIds);

    int increase(Long postId);

    void clear();
}
