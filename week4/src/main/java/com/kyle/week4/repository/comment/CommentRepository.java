package com.kyle.week4.repository.comment;

import com.kyle.week4.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(Long id);
    List<Comment> findAllInfiniteScroll(Long postId, int limit);
    List<Comment> findAllInfiniteScroll(Long postId, Long lastCommentId, int limit);
}
