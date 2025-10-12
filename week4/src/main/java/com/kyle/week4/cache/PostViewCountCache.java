package com.kyle.week4.cache;

public interface PostViewCountCache {
    int count(Long postId);
    int increase(Long postId);
    void backUp(Long postId);
    void clear();
}
