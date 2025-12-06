package com.kyle.week4.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomUserId implements Serializable {
    private Long chatRoomId;
    private Long userId;
}
