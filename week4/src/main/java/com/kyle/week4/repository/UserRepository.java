package com.kyle.week4.repository;

import com.kyle.week4.entity.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentHashMap<Long, User> database = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> emailIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> nicknameIndex = new ConcurrentHashMap<>();

    public User save(User user) {
        if (user.isNew()) {
            Long userId = primaryKey.getAndIncrement();
            user.assignId(userId);
        }
        database.put(user.getId(), user);
        emailIndex.put(user.getEmail(), user);
        nicknameIndex.put(user.getNickname(), user);
        return user;
    }

    public List<User> saveAll(Iterable<User> users) {
        List<User> result = new ArrayList<>();

        for (User user : users) {
            result.add(save(user));
        }

        return result;
    }

    public boolean existsByNickname(String nickname) {
        return nicknameIndex.containsKey(nickname);
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(emailIndex.get(email));
    }

    public void clear() {
        primaryKey.set(1);
        database.clear();
        emailIndex.clear();
        nicknameIndex.clear();
    }
}
