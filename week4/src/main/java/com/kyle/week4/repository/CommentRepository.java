package com.kyle.week4.repository;

import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.Post;
import org.springframework.stereotype.Repository;

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
}
