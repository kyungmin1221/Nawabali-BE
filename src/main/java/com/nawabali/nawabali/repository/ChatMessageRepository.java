package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<Chat.ChatMessage, Long> {

    Optional<List<Chat.ChatMessage>> findByChatRoomIdOrderByIdDesc(Long roomId);

    Optional<Object> findFirstBySenderOrderByCreatedMessageAtDesc(String nickname);

    Optional<Chat.ChatMessage> findByIdAndUserId(Long id, Long id1);

    Optional<List<Chat.ChatMessage>> findByChatRoomId(Long chatRoomId);

    List<Chat.ChatMessage> findByChatRoomIdOrderByCreatedMessageAtDesc(Long roomId);

    Optional<List<Chat.ChatMessage>> findByChatRoomIdAndIsReceiverReadFalse(Long id);
}