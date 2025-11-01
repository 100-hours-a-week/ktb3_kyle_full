package com.kyle.week4.repository.post;

import com.kyle.week4.entity.Post;
import com.kyle.week4.repository.MemoryClearRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class PostMemoryRepository implements PostRepository, MemoryClearRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentSkipListMap<Long, Post> database = new ConcurrentSkipListMap<>();
    private final ConcurrentHashMap<Long, ReentrantLock> commentCountLock = new ConcurrentHashMap<>();

    @Override
    public Post save(Post post) {
        if (post.isNew()) {
            Long postId = primaryKey.getAndIncrement();
            post.assignId(postId);
            commentCountLock.put(postId, new ReentrantLock());
        }
        database.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Post> findAllInfiniteScroll(int limit) {
        return database.descendingMap().values().stream()
          .filter(Post::isNotDeleted)
          .limit(limit)
          .toList();
    }

    @Override
    public List<Post> findAllInfiniteScroll(Long lastPostId, int limit) {
        return database.headMap(lastPostId, false).descendingMap().values().stream()
          .filter(Post::isNotDeleted)
          .limit(limit)
          .toList();
    }

    @Override
    public void increaseCommentCount(Long postId) {
        try {
            commentCountLock.get(postId).lock();
            database.get(postId).increaseCommentCount();
        } finally {
            commentCountLock.get(postId).unlock();
        }
    }

    @Override
    public boolean notExistsById(Long postId) {
        return !database.containsKey(postId) || database.get(postId).isDeleted();
    }

    @Override
    public Optional<Post> findLockedById(Long postId) {
        return Optional.ofNullable(database.get(postId));
    }

    @Override
    public Optional<Post> findWithUserAndPostImagesById(Long postId) {
        return Optional.ofNullable(database.get(postId));
    }

    @Override
    public void clear() {
        primaryKey.set(1);
        database.clear();
        commentCountLock.clear();
    }
}
