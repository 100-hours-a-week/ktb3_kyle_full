package com.kyle.week4.controller;

import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends ControllerTestSupport {

    private final MockHttpSession session = new MockHttpSession();

    @BeforeEach
    void setUp() throws Exception {
        given(authenticationProvider.isAuthenticated(any(), any()))
          .willReturn(true);

        session.setAttribute("userId", 1L);
    }

    @Test
    @DisplayName("새로운 게시글을 작성한다.")
    void createPost() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
          .title("제목입니다.")
          .content("내용입니다.")
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();

        // when // then
        mockMvc.perform(
            post("/posts")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 생성 시 제목은 비어있을 수 없다.")
    void titleNotEmpty() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
          .title("")
          .content("내용입니다.")
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();

        // when // then
        mockMvc.perform(
            post("/posts")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("제목은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("게시글 생성 시 제목은 최대 26자 까지 작성 가능하다.")
    void validationTitleLength() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
          .title("제목제목제목제목제목제목제목제목제목제목제목제목제목.")
          .content("내용입니다.")
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();

        // when // then
        mockMvc.perform(
            post("/posts")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("제목은 최대 26자 까지 작성 가능합니다."));
    }

    @Test
    @DisplayName("게시글 생성 시 내용은 비어있을 수 없다.")
    void contentNotEmpty() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
          .title("제목입니다.")
          .content("")
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();

        // when // then
        mockMvc.perform(
            post("/posts")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("내용은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("게시글 수정 시 제목은 비어있을 수 없다.")
    void titleNotEmpty_whenUpdate() throws Exception {
        // given
        PostUpdateRequest request = PostUpdateRequest.builder()
          .title("")
          .content("내용입니다.")
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();

        // when // then
        mockMvc.perform(
            patch("/posts/1")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("제목은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("게시글 수정 시 제목은 최대 26자 까지 작성 가능하다.")
    void validationTitleLength_whenUpdate() throws Exception {
        // given
        PostUpdateRequest request = PostUpdateRequest.builder()
          .title("제목제목제목제목제목제목제목제목제목제목제목제목제목.")
          .content("내용입니다.")
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();

        // when // then
        mockMvc.perform(
            patch("/posts/1")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("제목은 최대 26자 까지 작성 가능합니다."));
    }

    @Test
    @DisplayName("게시글 수정 시 내용은 비어있을 수 없다.")
    void contentNotEmpty_whenUpdate() throws Exception {
        // given
        PostUpdateRequest request = PostUpdateRequest.builder()
          .title("제목입니다.")
          .content("")
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();

        // when // then
        mockMvc.perform(
            patch("/posts/1")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("내용은 비어있을 수 없습니다."));
    }
}