package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.dto.ChatDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatDslRepositoryCustom {
    List <ChatDto.ChatRoomListResponseDto> findAllByUserId(Long userId);

    List <ChatDto.ChatRoomSearchListDto> queryRoomsByName(String roomName, Long userId);

    List <ChatDto.ChatRoomSearchListDto> queryRoomsByMessage(String roomName, Long userId);

    Long getUnreadMessageCountsForUser (String userName);
}
