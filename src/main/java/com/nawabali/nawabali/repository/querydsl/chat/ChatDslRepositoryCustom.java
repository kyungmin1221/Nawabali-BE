package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.dto.ChatDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatDslRepositoryCustom {
    Slice<ChatDto.ChatRoomListResponseDto> findAllByUserId(Long userId, Pageable pageable);

    Slice<ChatDto.ChatRoomSearchListDto> queryRoomsByName(String roomName, Long userId, Pageable pageable);

    Slice<ChatDto.ChatRoomSearchListDto> queryRoomsByMessage(String roomName, Long userId, Pageable pageable);

    Long getUnreadMessageCountsForUser (String userName);
}
