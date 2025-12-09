package com.kyle.week4.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomInfo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    private String lastChatMessage;
    private String opponentNickname;
    private String opponentProfileImage;
}
