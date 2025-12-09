package com.kyle.week4.entity.embedded;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kyle.week4.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ChatMessageCreateEvent {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    private Long senderId;
    private String lastChatMessage;
    private LocalDateTime lastChatMessageTime;

    @Builder
    public ChatMessageCreateEvent(Long roomId, Long senderId, String lastChatMessage, LocalDateTime lastChatMessageTime) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.lastChatMessage = lastChatMessage;
        this.lastChatMessageTime = lastChatMessageTime;
    }

    public static ChatMessageCreateEvent of(ChatMessage chatMessage) {
        return ChatMessageCreateEvent.builder()
            .roomId(chatMessage.getRoomId())
            .senderId(chatMessage.getSenderId())
            .lastChatMessage(chatMessage.getContent())
            .lastChatMessageTime(chatMessage.getCreatedAt())
            .build();
    }
}
