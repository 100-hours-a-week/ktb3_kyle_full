package com.kyle.week4.cache;

public interface PostViewCountCache {
    void initCache(Long postId);
    int count(Long postId);
    int increase(Long postId);
    void backUp(Long postId);
    void clear();
}
