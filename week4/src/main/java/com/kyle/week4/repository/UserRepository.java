package com.kyle.week4.repository;

import com.kyle.week4.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentHashMap<Long, User> database = new ConcurrentHashMap<>();

    public User save(User user) {
        if (user.isNew()) {
            Long userId = primaryKey.getAndIncrement();
            user.assignId(userId);
        }
        database.put(user.getId(), user);
        return user;
    }

    public boolean existsByNickname(String nickname) {
        return database.values().stream()
          .anyMatch(user -> user.isSameNickname(nickname));
    }

    public boolean existsByEmail(String email) {
        return database.values().stream()
          .anyMatch(user -> user.isSameEmail(email));
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return database.values().stream()
          .filter(user -> user.isSameEmail(email))
          .findFirst();
    }

    public void clear() {
        primaryKey.set(1);
        database.clear();
    }
}
