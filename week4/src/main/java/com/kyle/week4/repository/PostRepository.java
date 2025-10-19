package com.kyle.week4.repository;

import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(Long id);
    List<Post> findAllInfiniteScroll(int limit);
    List<Post> findAllInfiniteScroll(Long lastPostId, int limit);
    void increaseCommentCount(Long postId);
    boolean notExistsById(Long postId);
    void clear();
}
