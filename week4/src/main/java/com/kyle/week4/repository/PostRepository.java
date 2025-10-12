package com.kyle.week4.repository;

import com.kyle.week4.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class PostRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentSkipListMap<Long, Post> database = new ConcurrentSkipListMap<>();
    private final ConcurrentHashMap<Long, ReentrantLock> commentCountLock = new ConcurrentHashMap<>();

    public Post save(Post post) {
        if (post.isNew()) {
            Long postId = primaryKey.getAndIncrement();
            post.assignId(postId);
            commentCountLock.put(postId, new ReentrantLock());
        }
        database.put(post.getId(), post);
        return post;
    }

    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    public List<Post> findAllInfiniteScroll(int limit) {
        return database.descendingMap().values().stream()
          .filter(Post::isNotDeleted)
          .limit(limit)
          .toList();
    }

    public List<Post> findAllInfiniteScroll(Long lastPostId, int limit) {
        return database.headMap(lastPostId, false).descendingMap().values().stream()
          .filter(Post::isNotDeleted)
          .limit(limit)
          .toList();
    }

    public void increaseCommentCount(Long postId) {
        try {
            commentCountLock.get(postId).lock();
            database.get(postId).increaseCommentCount();
        } finally {
            commentCountLock.get(postId).unlock();
        }
    }

    public boolean notExistsById(Long postId) {
        return !database.containsKey(postId) || database.get(postId).isDeleted();
    }

    public void clear() {
        primaryKey.set(1);
        database.clear();
        commentCountLock.clear();
    }
}
