package com.kyle.week4.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kyle.week4.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    public ChatMessageResponse(Long id, Long senderId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static ChatMessageResponse of(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
            .id(chatMessage.getId())
            .content(chatMessage.getContent())
            .senderId(chatMessage.getSenderId())
            .createdAt(chatMessage.getCreatedAt())
            .build();
    }
}
