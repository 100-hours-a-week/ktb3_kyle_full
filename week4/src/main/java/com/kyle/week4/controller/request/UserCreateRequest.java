package com.kyle.week4.controller.request;

import com.kyle.week4.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateRequest {
    @Email(message = "올바른 이메일 주소 형식을 입력해주세요.")
    private String email;

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하 까지 가능합니다.")
    @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>,.?/]).+$",
      message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 1자 이상 포함해야 합니다."
    )
    private String password;

    @Size(max = 10, message = "닉네임은 최대 10자 까지 작성 가능합니다.")
    @NotBlank(message = "닉네임을 작성하지 않거나, 공백을 포함할 수 없습니다.")
    private String nickname;

    private String profileImage;

    @Builder
    public UserCreateRequest(String email, String password, String nickname, String profileImage) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public User toEntity() {
        return User.builder()
          .email(email)
          .nickname(nickname)
          .profileImage(profileImage)
          .build();
    }
}
