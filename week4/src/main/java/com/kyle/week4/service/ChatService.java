package com.kyle.week4.service;

import com.kyle.week4.controller.response.ChatRoomSummary;
import com.kyle.week4.entity.ChatRoom;
import com.kyle.week4.entity.ChatRoomUser;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.exception.ErrorCode;
import com.kyle.week4.repository.chat.ChatRoomRepository;
import com.kyle.week4.repository.chat.ChatRoomUserRepository;
import com.kyle.week4.repository.user.UserRepository;
import com.kyle.week4.utils.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final Snowflake snowflake;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    @Transactional
    public Long createChatRoom(Long userId, Long opponentId) {
        System.out.println("userId = " + userId +  " opponentId = " + opponentId);
        Long chatRoomId = snowflake.nextId();
        ChatRoom chatRoom = new ChatRoom(chatRoomId);
        chatRoomRepository.save(chatRoom);

        ChatRoomUser chatRoomUser = createChatRoomUser(userId, chatRoom);
        ChatRoomUser chatRoomOpponent = createChatRoomUser(opponentId, chatRoom);

        List<ChatRoomUser> chatRoomUsers = Arrays.asList(chatRoomUser, chatRoomOpponent);
        chatRoomUserRepository.saveAll(chatRoomUsers);
        return chatRoomId;
    }

    public List<ChatRoomSummary> getChatRoomList(Long userId) {
        return chatRoomUserRepository.findChatRoomsWithOpponent(userId);
    }

    public Map<Long, String> getNicknameMap(Long roomId) {
        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findByRoomId(roomId);
        return chatRoomUsers.stream().collect(
            Collectors.toMap(
                chatRoomUser -> chatRoomUser.getUser().getId(),
                chatRoomUser -> chatRoomUser.getUser().getNickname()
            )
        );
    }

    private ChatRoomUser createChatRoomUser(Long userId, ChatRoom chatRoom) {
        User user = findUserBy(userId);

        return ChatRoomUser.builder()
            .user(user)
            .chatRoom(chatRoom)
            .build();
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
