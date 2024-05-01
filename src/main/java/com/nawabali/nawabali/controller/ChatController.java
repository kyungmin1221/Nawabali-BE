package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Tag(name = "채팅 API", description = "채팅 관련 API 입니다.")
public class ChatController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅(DM)", description = "MessageType : ENTER 입장 | TALK 메세지 전송")
    @MessageMapping("/chat/message/{chatRoomId}")
    public void message(@DestinationVariable Long chatRoomId,
                        @Payload ChatDto.ChatMessageDto message,
                        Principal principal) {
        chatMessageService.message(chatRoomId, message, principal);
    }
}