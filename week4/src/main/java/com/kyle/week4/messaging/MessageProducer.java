package com.kyle.week4.messaging;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.controller.response.ChatMessageResponse;

public interface MessageProducer {

    void convertAndSend(String destination, ChatMessageResponse message);
}
