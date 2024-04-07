package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<Chat.ChatMessage, Long> {
    Optional<List<Chat.ChatMessage>> findByChatRoomIdAndUserId(Long roomId, Long userId);

//    Optional <List<ChatDto.ChatMessageDto>> findByRoomIdAndUserIdOrderByCreatedAtDesc(Long roomId, Long id);
//    Optional<List<ChatDto.ChatMessageDto>> findByChatRoom_IdAndUser_IdOrderByCreatedAtDesc(Long chatRoomId, Long userId);



}