package com.kyle.week4.security;

import com.kyle.week4.entity.User;
import com.kyle.week4.exception.ErrorCode;
import com.kyle.week4.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        return new CustomUserDetails(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            Collections.emptyList() // FIXME: 권한 추후 설정
        );
    }
}
