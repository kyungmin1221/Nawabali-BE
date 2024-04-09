package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Tag(name = "채팅 API", description = "채팅 관련 API 입니다.")
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    @Operation(summary = "채팅(DM) 전송" , description = "MessageType : ENTER : 입장, TALK : 메세지 전송")
    @MessageMapping("/chat/message")
    public void message(ChatDto.ChatMessageDto message) {
        if (ChatDto.ChatMessageDto.MessageType.ENTER.equals(message.getType()))
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");

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