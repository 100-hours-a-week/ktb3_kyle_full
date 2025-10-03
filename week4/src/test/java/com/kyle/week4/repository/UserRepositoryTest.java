package com.kyle.week4.repository;

import com.kyle.week4.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자의 식별자는 저장되는 순서대로 1씩 증가하는 값을 가진다.")
    void save() {
        // given
        User user1 = createUser("user1@example.com", "user1", "image1");
        User user2 = createUser("user2@example.com", "user2", "image2");
        User user3 = createUser("user3@example.com", "user3", "image3");

        // when
        Long user1Id = userRepository.save(user1);
        Long user2Id = userRepository.save(user2);
        Long user3Id = userRepository.save(user3);

        // then
        assertThat(user1Id).isEqualTo(1);
        assertThat(user2Id).isEqualTo(2);
        assertThat(user3Id).isEqualTo(3);
    }

    private User createUser(String email, String nickname, String profileImage) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .profileImage(profileImage)
            .build();
    }
}