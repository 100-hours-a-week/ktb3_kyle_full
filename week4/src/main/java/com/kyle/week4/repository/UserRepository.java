package com.kyle.week4.repository;

import com.kyle.week4.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    List<User> saveAll(Iterable<User> users);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void clear();
}
