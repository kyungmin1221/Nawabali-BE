package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("/chat/message")
    public void message(ChatDto.ChatMessageDto message) {
        if (ChatDto.ChatMessageDto.MessageType.ENTER.equals(message.getType()))
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");

//        User userOptional = userRepository.findById(message.getUserId())
//                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatMessage chatMessage = Chat.ChatMessage.builder()
                .type(message.getType())
                .sender(message.getSender())
                .message(message.getMessage())
                .createdAt(LocalDateTime.now())
//                .user(userOptional)
                .chatRoom(chatRoom)
                .build();

        chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @GetMapping("/chat/room/{roomId}/message")
    public List<ChatDto.ChatMessageDto> loadMessage (@PathVariable Long roomId,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatMessageService.loadMessage(roomId, userDetails.getUser());
    }
}