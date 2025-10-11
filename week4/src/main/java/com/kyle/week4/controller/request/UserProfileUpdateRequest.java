package com.kyle.week4.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileUpdateRequest {
    @Size(max = 10, message = "닉네임은 최대 10자 까지 작성 가능합니다.")
    @NotBlank(message = "닉네임을 작성하지 않거나, 공백을 포함할 수 없습니다.")
    private String nickname;
    private String profileImage;

    public UserProfileUpdateRequest(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
