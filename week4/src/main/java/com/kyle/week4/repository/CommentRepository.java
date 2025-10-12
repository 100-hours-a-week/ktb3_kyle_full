package com.kyle.week4.repository;

import com.kyle.week4.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CommentRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentSkipListMap<Long, Comment> database = new ConcurrentSkipListMap<>();

    public Comment save(Comment comment) {
        if (comment.isNew()) {
            Long commentId = primaryKey.getAndIncrement();
            comment.assignId(commentId);
        }
        database.put(comment.getId(), comment);
        return comment;
    }

    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    public List<Comment> findAllInfiniteScroll(Long postId, int limit) {
        return database.values().stream()
          .filter(comment -> comment.canDisplay(postId))
          .limit(limit)
          .toList();
    }

    public List<Comment> findAllInfiniteScroll(Long postId, Long lastCommentId, int limit) {
        return database.tailMap(lastCommentId, false).values().stream()
          .filter(comment -> comment.canDisplay(postId))
          .limit(limit)
          .toList();
    }

    public void clear() {
        primaryKey.set(1);
        database.clear();
    }
}
