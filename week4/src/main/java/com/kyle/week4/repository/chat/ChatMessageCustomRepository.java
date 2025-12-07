package com.kyle.week4.repository.chat;

import com.kyle.week4.entity.ChatMessage;

import java.util.List;

public interface ChatMessageCustomRepository {
    List<ChatMessage> findBeforeMessagesByRoomId(Long roomId, Long lastMessageId, int limit);
}
