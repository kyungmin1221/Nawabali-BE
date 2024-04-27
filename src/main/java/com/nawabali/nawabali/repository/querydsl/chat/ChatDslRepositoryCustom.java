package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.dto.ChatDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatDslRepositoryCustom {

    Slice<ChatDto.ChatRoomListDto> findAllByUserId(Long userId, Pageable pageable);

    List<ChatDto.ChatRoomListDto> queryRoomsByName(String roomName, Long userId);

    List<ChatDto.ChatRoomListDto> queryRoomsByMessage(String roomName, Long userId);

    Long getUnreadMessageCountsForUser (String userName);

}
