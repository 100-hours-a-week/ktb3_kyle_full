package com.kyle.week4.repository;

import com.kyle.week4.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentHashMap<Long, User> database = new ConcurrentHashMap<>();

    public Long save(User user) {
        Long userId = primaryKey.getAndIncrement();
        user.setUserId(userId);
        database.putIfAbsent(userId, user);
        return userId;
    }
}
