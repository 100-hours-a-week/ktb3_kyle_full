package com.kyle.week4.service;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.controller.response.ChatMessageResponse;
import com.kyle.week4.entity.ChatMessage;
import com.kyle.week4.entity.embedded.ChatMessageCreateEvent;
import com.kyle.week4.messaging.MessageProducer;
import com.kyle.week4.repository.chat.ChatMessageRepository;
import com.kyle.week4.repository.chat.ChatRoomRepository;
import com.kyle.week4.utils.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final Snowflake snowflake;
    private final MessageProducer messageProducer;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createMessage(Long roomId, ChatMessageRequest request, LocalDateTime createdAt) {
        Long chatMessageId = snowflake.nextId();
        ChatMessage chatMessage = ChatMessage.builder()
            .id(chatMessageId)
            .roomId(roomId)
            .senderId(request.getSenderId())
            .content(request.getContent())
            .createdAt(createdAt)
            .build();

        chatMessageRepository.save(chatMessage); // mongoDB
        messageProducer.convertAndSend("/sub/room/" + roomId, ChatMessageResponse.of(chatMessage)); // Message Broker
        chatRoomRepository.updateLastChatMessageId(roomId, chatMessageId); // MySQL

        // 참가자에게 최신 메시지 전송
        eventPublisher.publishEvent(ChatMessageCreateEvent.of(chatMessage));
    }

    public List<ChatMessageResponse> getMessages(Long roomId, Long lastMessageId, int limit) {
        List<ChatMessage> messages = chatMessageRepository.findBeforeMessagesByRoomId(roomId, lastMessageId, limit);
        return messages.stream()
            .sorted(Comparator.comparing(ChatMessage::getId))
            .map(ChatMessageResponse::of)
            .toList();
    }
}
