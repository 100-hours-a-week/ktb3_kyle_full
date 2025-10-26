package com.kyle.week4.repository;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class UserNamedLock {
    private final ConcurrentHashMap<String, ReentrantLock> namedLocks = new ConcurrentHashMap<>();

    public boolean lock(String lockName) {
        ReentrantLock lock = namedLocks.putIfAbsent(lockName, new ReentrantLock());

        if (lock == null) {
            return false;
        }

        lock.lock();
        return true;
    }

    public void releaseLock(String lockName) {
        namedLocks.computeIfPresent(lockName, (key, value) -> {
            value.unlock();
            namedLocks.remove(key);
            return value;
        });
    }
}
