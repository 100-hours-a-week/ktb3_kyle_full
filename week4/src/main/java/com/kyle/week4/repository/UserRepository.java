package com.kyle.week4.repository;

import com.kyle.week4.domain.User;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentHashMap<Long, User> database = new ConcurrentHashMap<>();

    public Long save(User user) {
        Long userId = primaryKey.getAndIncrement();
        user.assignUserId(userId);
        database.putIfAbsent(userId, user);
        return userId;
    }

    public boolean existsByNickname(String nickname) {
        return database.values().stream()
          .anyMatch(user -> user.isDuplicateNickname(nickname));
    }

    public boolean existsByEmail(String email) {
        return database.values().stream()
          .anyMatch(user -> user.isDuplicateEmail(email));
    }

    public void clear() {
        primaryKey.set(1);
        database.clear();
    }
}
