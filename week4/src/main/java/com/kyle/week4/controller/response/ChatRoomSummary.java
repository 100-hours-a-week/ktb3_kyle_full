package com.kyle.week4.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomSummary {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    private String opponentNickname;
    private String opponentProfileImage;
}
