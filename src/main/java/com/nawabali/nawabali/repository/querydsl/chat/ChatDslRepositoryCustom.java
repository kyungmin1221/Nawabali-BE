package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.dto.ChatDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatDslRepositoryCustom {

    Slice<ChatDto.ChatRoomListDto> findAllByUserId(Long userId, Pageable pageable);

    Slice <ChatDto.ChatRoomListDto> findChatRoomByRoomName (String roomName, Pageable pageable);

    Long getUnreadMessageCountsForUser (String userName);

}
