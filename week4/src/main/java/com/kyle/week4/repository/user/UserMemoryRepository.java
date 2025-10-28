package com.kyle.week4.repository.user;

import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.MemoryClearRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.kyle.week4.exception.ErrorCode.DUPLICATE_EMAIL_ERROR;
import static com.kyle.week4.exception.ErrorCode.DUPLICATE_NICKNAME_ERROR;

@Repository
public class UserMemoryRepository implements UserRepository, MemoryClearRepository {
    private final AtomicLong primaryKey = new AtomicLong(1);
    private final ConcurrentHashMap<Long, User> database = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> emailIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> nicknameIndex = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.isNew()) {
            User existByEmail = emailIndex.putIfAbsent(user.getEmail(), user);
            if (existByEmail != null) { throw new CustomException(DUPLICATE_EMAIL_ERROR); }

            User existingByNickname = nicknameIndex.putIfAbsent(user.getNickname(), user);
            if (existingByNickname != null) {
                emailIndex.remove(user.getEmail());
                throw new CustomException(DUPLICATE_NICKNAME_ERROR);
            }

            Long userId = primaryKey.getAndIncrement();
            user.assignId(userId);
            database.put(user.getId(), user);
        }
        else {
            database.put(user.getId(), user);
            emailIndex.put(user.getEmail(), user);
            nicknameIndex.put(user.getNickname(), user);
        }
        return user;
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return nicknameIndex.containsKey(nickname);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(emailIndex.get(email));
    }

    @Override
    public List<User> saveAll(Iterable<User> users) {
        List<User> result = new ArrayList<>();

        for (User user : users) {
            result.add(save(user));
        }
        return result;
    }

    @Override
    public void clear() {
        primaryKey.set(1);
        database.clear();
        emailIndex.clear();
        nicknameIndex.clear();
    }
}
