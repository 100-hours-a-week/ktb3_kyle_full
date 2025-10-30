package com.kyle.week4.entity;

import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(length = 10, unique = true)
    private String nickname;

    @Column(length = 200)
    private String profileImage;

    @Builder
    public User(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public boolean isSameNickname(String nickname) {
        return this.nickname.equals(nickname);
    }

    public boolean isSameEmail(String email) {
        return this.email.equals(email);
    }

    public boolean isNew() {
        return id == null;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateUserProfile(UserProfileUpdateRequest request) {
        this.nickname = request.getNickname();
        this.profileImage = request.getProfileImage();
    }
}
