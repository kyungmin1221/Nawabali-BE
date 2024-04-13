package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.MessageType;
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
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
@Slf4j(topic = "ChatMessageService 로그")
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationService notificationService;

    // 입장
    public void enterMessage(ChatDto.ChatMessageDto message) {

        User userOptional = userRepository.findById(message.getUserId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        if (MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message); }

        if (!chatRoomRepository.findByIdAndUserId(chatRoom.getId(),userOptional.getId()).isPresent()){

            Chat.ChatRoom chatRoomSave = Chat.ChatRoom.builder()
                    .name(chatRoom.getName())
                    .roomNumber(chatRoom.getRoomNumber())
                    .user(userOptional)
                    .build();

            chatRoomRepository.save(chatRoomSave);
        }

    }

    // 메세지 보내기
    public void message(ChatDto.ChatMessageDto message) {

        User userOptional = userRepository.findById(message.getUserId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatMessage chatMessage = Chat.ChatMessage.builder()
                .type(message.getType())
                .sender(message.getSender())
                .message(message.getMessage())
                .createdAt(LocalDateTime.now())
                .user(userOptional)
                .chatRoom(chatRoom)
                .build();

        chatMessageRepository.save(chatMessage);

        notificationService.notifyMessage(chatRoom.getRoomNumber(), message.getUserId(), message.getSender());

        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);

    }
}