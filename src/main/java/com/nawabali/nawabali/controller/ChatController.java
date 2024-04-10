package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.constant.MessageType;
import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.service.ChatMessageService;
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

    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방(DM) 입장" , description = "MessageType : ENTER _ 입장")
    @MessageMapping("/chat/enter/message")
    public void enterMessage(ChatDto.ChatMessageDto message) {

        chatMessageService.enterMessage(message);

    }

    @Operation(summary = "채팅(DM) 전송" , description = "MessageType : TALK _ 메세지 전송")
    @MessageMapping("/chat/message")
    public void message(ChatDto.ChatMessageDto message) {

        chatMessageService.message(message);

    }
}