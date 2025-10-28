package com.kyle.week4.repository.user;

import com.kyle.week4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
