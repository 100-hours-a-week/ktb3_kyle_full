package com.kyle.week4.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "chat_message")
public class ChatMessage {
    @Id
    private Long id;

    private Long roomId;

    private Long senderId;

    private String content;

    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(Long id, Long roomId, Long senderId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
