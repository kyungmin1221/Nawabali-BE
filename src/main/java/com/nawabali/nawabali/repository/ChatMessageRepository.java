package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<Chat.ChatMessage, Long> {

    Optional<Slice<Chat.ChatMessage>> findByChatRoomIdOrderByIdDesc(Long roomId, Pageable pageable);
    Optional <List<Chat.ChatMessage>> findByChatRoomIdOrderByCreatedMessageAtDesc(Long roomId);
    Optional<List<Chat.ChatMessage>> findByChatRoomIdAndIsReceiverReadFalse(Long id);

    Optional<Slice<Chat.ChatMessage>> findByChatRoomIdAndUserIdOrderByIdDesc(Long chatRoomId, Long userId, Pageable pageable);
}