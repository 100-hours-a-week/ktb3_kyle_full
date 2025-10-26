package com.kyle.week4.repository;

import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @Test
    @DisplayName("동시에 같은 이메일/닉네임으로 저장을 시도하면 정확히 하나만 성공해야 한다")
    void concurrentSave_duplicateEmail_shouldAllowOnlyOne() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            results.add(executor.submit(() -> {
                readyLatch.countDown(); // 스레드 준비 완료
                startLatch.await();     // 모든 스레드 동시 시작
                try {
                    User user = createUser("same@example.com", "same", "image.jpg");
                    userRepository.save(user);
                    return true; // 저장 성공
                } catch (CustomException e) {
                    return false; // 중복 예외 발생 (정상 동작)
                } finally {
                    doneLatch.countDown();
                }
            }));
        }

        // 모든 스레드가 준비될 때까지 대기
        readyLatch.await();
        // 스레드 동시에 시작
        startLatch.countDown();
        // 스레드 종료 대기
        doneLatch.await();
        executor.shutdown();

        // then: 성공한 스레드는 정확히 1명이어야 함
        long successCount = results.stream()
                .filter(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        assertThat(successCount).isEqualTo(1);
        assertThat(userRepository.existsByEmail("same@example.com")).isTrue();
        assertThat(userRepository.existsByNickname("same")).isTrue();
    }

    private User createUser(String email, String nickname, String profileImage) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}