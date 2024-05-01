package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.dto.ChatDto.ChatRoomListResponseDto;
import com.nawabali.nawabali.dto.ChatDto.ChatRoomSearchListDto;

import java.util.List;

public interface ChatDslRepositoryCustom {
    List <ChatRoomListResponseDto> findAllByUserId(Long userId);

    List <ChatRoomSearchListDto> queryRoomsByName(String roomName, Long userId);

    List<ChatRoomSearchListDto> queryRoomsByMessage(String roomName, Long userId);

    Long getUnreadMessageCountsForUser (String userName);
}
