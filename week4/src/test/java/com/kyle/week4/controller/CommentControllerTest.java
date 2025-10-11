package com.kyle.week4.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.controller.request.CommentUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends ControllerTestSupport {

    private final MockHttpSession session = new MockHttpSession();

    @BeforeEach
    void setUp() throws Exception {
        given(authenticationProvider.isAuthenticated(any(), any()))
          .willReturn(true);

        session.setAttribute("userId", 1L);
    }

    @Test
    @DisplayName("댓글을 작성한다.")
    void createComment() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest("댓글");

        // when // then
        mockMvc.perform(
            post("/posts/1/comments")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("댓글 작성 시 내용은 비어있을 수 없다.")
    void createComment_contentNotEmpty() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest("");

        // when // then
        mockMvc.perform(
            post("/posts/1/comments")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("댓글 내용은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("댓글 수정 시 내용은 비어있을 수 없다.")
    void updateComment_contentNotEmpty() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("");

        // when // then
        mockMvc.perform(
            patch("/posts/1/comments/1")
              .session(session)
              .content(objectMapper.writeValueAsString(request))
              .contentType(MediaType.APPLICATION_JSON)
          )
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").isEmpty())
          .andExpect(jsonPath("$.errorMessage").value("댓글 내용은 비어있을 수 없습니다."));
    }
}