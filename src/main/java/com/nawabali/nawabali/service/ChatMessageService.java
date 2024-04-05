package com.nawabali.nawabali.service;

import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j(topic = "ChatMessageService 로그")
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;




    // 대화 조회
    public List<ChatDto.ChatMessageDto> loadMessage(Long roomId, User user) {

        User userOptional = userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        List<Chat.ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        // ChatMessage를 ChatDto.ChatMessage로 변환하여 반환
        return chatMessages.stream()
                .map(chatMessage -> ChatDto.ChatMessageDto.builder()
                        .id(chatMessage.getId())
                        .type(chatMessage.getType())
                        .sender(chatMessage.getSender())
                        .message(chatMessage.getMessage())
                        .createdAt(chatMessage.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

}