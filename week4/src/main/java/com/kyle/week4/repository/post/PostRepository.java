package com.kyle.week4.repository.post;

import com.kyle.week4.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(Long id);
    List<Post> findAllInfiniteScroll(int limit);
    List<Post> findAllInfiniteScroll(Long lastPostId, int limit);
    void increaseCommentCount(Long postId);
    boolean notExistsById(Long postId);
    Optional<Post> findLockedById(Long postId);
    Optional<Post> findWithUserAndPostImagesById(Long postId);
}
