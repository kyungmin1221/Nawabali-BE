package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

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
    public ChatDto.ChatMessageResponseDto message(ChatDto.ChatMessageDto message) {

        return chatMessageService.message(message);


    }
}