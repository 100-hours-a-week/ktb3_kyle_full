package com.kyle.week4.messaging.simple;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.messaging.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpMessageProducer implements MessageProducer {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void convertAndSend(String destination, ChatMessageRequest message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}
