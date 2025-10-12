package com.kyle.week4.cache;

import org.springframework.stereotype.Component;

public interface PostLikeCountCache {
    void initCache(Long postId);
    int count(Long postId);
    int increase(Long postId);
    int decrease(Long postId);
    void backUp(Long postId);
    void clear();
}
