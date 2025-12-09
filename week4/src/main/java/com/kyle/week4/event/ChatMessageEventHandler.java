package com.kyle.week4.event;

import com.kyle.week4.entity.ChatRoomUser;
import com.kyle.week4.entity.embedded.ChatMessageCreateEvent;
import com.kyle.week4.messaging.MessageProducer;
import com.kyle.week4.repository.chat.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageEventHandler {
    private final MessageProducer messageProducer;
    private final ChatRoomUserRepository chatRoomUserRepository;

    @Async("eventRelayExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChatMessageCreated(ChatMessageCreateEvent event) {
        log.info("[EventListener:handleChatMessageCreated] event: {}", event.toString());
        List<ChatRoomUser> participants = chatRoomUserRepository.findParticipants(event.getSenderId(), event.getRoomId());

        // 비동기 처리 추가하기
        participants.forEach(participant -> {
            messageProducer.convertAndSendNewMessage("/sub/new-message/" + participant.getUser().getId(), event);
        });
    }
}
