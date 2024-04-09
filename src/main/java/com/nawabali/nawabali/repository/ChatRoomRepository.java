package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<Chat.ChatRoom, Long> {
    Optional<List<Chat.ChatRoom>> findByNameContainingIgnoreCase(String name);
    Chat.ChatRoom findByRoomNumber(String roomNumber);
}