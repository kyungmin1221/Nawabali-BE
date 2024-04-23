package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.repository.querydsl.chat.ChatDslRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<Chat.ChatRoom, Long>, ChatDslRepositoryCustom {
    Optional<List<Chat.ChatRoom>> findByRoomNameContainingIgnoreCase(String roomName);
    Chat.ChatRoom findByRoomNumber(String roomNumber);

//    Optional<List<Chat.ChatRoom>> findAllByUserId(Long userId);

    Optional<List<Chat.ChatRoom>> findByIdAndUserId(Long id, Long userId);

    Optional <Chat.ChatRoom> findByUserIdAndOtherUserId(Long userId, Long otherUserId);
}