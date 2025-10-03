package com.kyle.week4.utils;

import com.kyle.week4.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderTest {
    private final PasswordEncoder encoder = new PasswordEncoder();

    @Test
    @DisplayName("원본 비밀번호는 검증 시 일치해야 한다.")
    void matchesSuccess() {
        String password = "plain-password";
        String encodedPassword = encoder.encode(password);
        assertThat(encoder.matches(password, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("원본과 다른 비밀번호는 검증 시 일치하면 안된다.")
    void matchesFailure() {
        String password = "plain-password";
        String otherPassword = "other-password";
        String encodedPassword = encoder.encode(password);
        assertThat(encoder.matches(otherPassword, encodedPassword)).isFalse();
    }
}