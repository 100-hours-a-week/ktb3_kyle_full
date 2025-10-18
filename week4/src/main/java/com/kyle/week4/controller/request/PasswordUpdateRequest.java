package com.kyle.week4.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청 DTO")
public class PasswordUpdateRequest {
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하 까지 가능합니다.")
    @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>,.?/]).+$",
      message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 1자 이상 포함해야 합니다."
    )
    @Schema(description = "비밀번호", defaultValue = "Kyle12345~")
    private String password;
}
