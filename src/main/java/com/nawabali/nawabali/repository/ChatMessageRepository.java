package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<Chat.ChatMessage, Long> {

    Optional<List<Chat.ChatMessage>> findByChatRoomIdAndUserId(Long roomId, Long userId);

    Optional<Object> findFirstBySenderOrderByCreatedAtDesc(String nickname);

}