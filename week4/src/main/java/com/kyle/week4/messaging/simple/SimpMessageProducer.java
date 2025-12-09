package com.kyle.week4.messaging.simple;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.controller.response.ChatMessageResponse;
import com.kyle.week4.entity.embedded.ChatMessageCreateEvent;
import com.kyle.week4.messaging.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpMessageProducer implements MessageProducer {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void convertAndSend(String destination, ChatMessageResponse message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    @Override
    public void convertAndSendNewMessage(String destination, ChatMessageCreateEvent event) {
        messagingTemplate.convertAndSend(destination, event);
    }
}
