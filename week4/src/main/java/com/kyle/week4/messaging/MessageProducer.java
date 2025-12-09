package com.kyle.week4.messaging;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.controller.response.ChatMessageResponse;
import com.kyle.week4.entity.embedded.ChatMessageCreateEvent;

public interface MessageProducer {

    void convertAndSend(String destination, ChatMessageResponse message);
    void convertAndSendNewMessage(String destination, ChatMessageCreateEvent event);
}
