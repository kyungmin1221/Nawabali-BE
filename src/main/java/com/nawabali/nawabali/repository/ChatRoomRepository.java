package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.repository.querydsl.chat.ChatDslRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<Chat.ChatRoom, Long>, ChatDslRepositoryCustom {
    Optional <Chat.ChatRoom> findByUserIdAndOtherUserId(Long userId, Long otherUserId);
    Long getUnreadMessageCountsForUser(String nickname);
}