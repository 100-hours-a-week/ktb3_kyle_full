package com.kyle.week4.controller;

import com.kyle.week4.controller.request.UserCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("새로운 사용자를 등록한다.")
    void createUser() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
          .email("kyle@example.com")
          .password("Kyle1234!")
          .nickname("kyle")
          .profileImage("https://image.kr/img.jpg")
          .build();

        // when // then
        mockMvc.perform(
            post("/users")
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.httpStatus").value("CREATED"))
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data").isNumber())
          .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("회원가입 시 이메일은 비어있을 수 없다.")
    void validationEmailIsEmpty() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
          .email("")
          .password("Kyle1234!")
          .nickname("kyle")
          .profileImage("https://image.kr/img.jpg")
          .build();

        // when // then
        mockMvc.perform(
            post("/users")
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("이메일은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("회원가입 시 올바른 이메일 주소 형식을 지켜야 한다.")
    void validationEmail() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
          .email("kyleexample.com")
          .password("Kyle1234!")
          .nickname("kyle")
          .profileImage("https://image.kr/img.jpg")
          .build();

        // when // then
        mockMvc.perform(
            post("/users")
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("올바른 이메일 주소 형식을 입력해주세요."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Kyle12!", "Kyle12!Kyle12!Kyle12!"})
    @DisplayName("회원가입 시 비밀번호는 8자 이상, 20자 이하여야 한다.")
    void validationPassword(String password) throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
          .email("kyle@example.com")
          .password(password)
          .nickname("kyle")
          .profileImage("https://image.kr/img.jpg")
          .build();

        // when // then
        mockMvc.perform(
            post("/users")
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("비밀번호는 8자 이상 20자 이하 까지 가능합니다."));
    }

    @Test
    @DisplayName("회원가입 시 비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 1자 이상 포함해야 한다.")
    void validationPasswordRegex() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
          .email("kyle@example.com")
          .password("Kyle1234")
          .nickname("kyle")
          .profileImage("https://image.kr/img.jpg")
          .build();

        // when // then
        mockMvc.perform(
            post("/users")
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 1자 이상 포함해야 합니다."));
    }

    @Test
    @DisplayName("회원가입 시 닉네임은 최대 10자 까지 작성할 수 있다.")
    void validationNickname() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
          .email("kyle@example.com")
          .password("Kyle1234!")
          .nickname("kyle1234567")
          .profileImage("https://image.kr/img.jpg")
          .build();

        // when // then
        mockMvc.perform(
            post("/users")
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("닉네임은 최대 10자 까지 작성 가능합니다."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("회원가입 시 닉네임을 작성하지 않거나 공백을 포함할 수 없다.")
    void validationNicknameNotBlank(String nickname) throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
          .email("kyle@example.com")
          .password("Kyle123!")
          .nickname(nickname)
          .profileImage("https://image.kr/img.jpg")
          .build();

        // when // then
        mockMvc.perform(
            post("/users")
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("닉네임을 작성하지 않거나, 공백을 포함할 수 없습니다."));
    }
}