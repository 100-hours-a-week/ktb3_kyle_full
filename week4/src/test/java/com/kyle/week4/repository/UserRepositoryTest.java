package com.kyle.week4.repository;

import com.kyle.week4.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.clear();
    }

    @Test
    @DisplayName("사용자의 식별자는 저장되는 순서대로 1씩 증가하는 값을 가진다.")
    void save() {
        // given
        User user1 = createUser("user1@example.com", "user1", "image1");
        User user2 = createUser("user2@example.com", "user2", "image2");
        User user3 = createUser("user3@example.com", "user3", "image3");

        // when
        Long user1Id = userRepository.save(user1).getId();
        Long user2Id = userRepository.save(user2).getId();
        Long user3Id = userRepository.save(user3).getId();

        // then
        assertThat(user2Id).isEqualTo(user1Id + 1);
        assertThat(user3Id).isEqualTo(user2Id + 1);
    }

    @Test
    @DisplayName("닉네임이 이미 존재하는 경우 중복으로 판단한다.")
    void existsByNickname() {
        // given
        String nickname = "test";
        User user = createUser("user@example.com", nickname, "image");
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByNickname(nickname);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이메일이 이미 존재하는 경우 중복으로 판단한다.")
    void existsByEmail() {
        // given
        String email = "user@example.com";
        User user = createUser(email, "test", "image");
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByEmail(email);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("식별자로 사용자를 조회한다.")
    void findById() {
        // given
        User user = createUser("user1@example.com", "user1", "image1");
        Long userId = userRepository.save(user).getId();

        // when
        Optional<User> findUser = userRepository.findById(userId);

        // then
        assertThat(findUser).isPresent().get()
          .extracting("email", "nickname", "profileImage")
          .contains("user1@example.com", "user1", "image1");
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다.")
    void findByEmail() {
        // given
        String email = "user1@example.com";
        User user = createUser(email, "user1", "image1");
        userRepository.save(user);

        // when
        Optional<User> findUser = userRepository.findByEmail(email);

        // then
        assertThat(findUser).isPresent().get()
          .extracting("email", "nickname", "profileImage")
          .contains(email, "user1", "image1");
    }

    private User createUser(String email, String nickname, String profileImage) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .profileImage(profileImage)
            .build();
    }
}