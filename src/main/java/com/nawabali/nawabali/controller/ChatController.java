package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.List;

@Tag(name = "채팅 API", description = "채팅 관련 API 입니다.")
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessageSendingOperations messagingTemplate;

//    @Operation(summary = "채팅방(DM) 입장" , description = "MessageType : ENTER _ 입장")
//    @MessageMapping("/chat/enter/message")
//    public void enterMessage(ChatDto.ChatMessageDto message) {
//
//        chatMessageService.enterMessage(message);
//
//    }

    @Operation(summary = "채팅(DM) 전송" , description = "MessageType : TALK _ 메세지 전송")
    @MessageMapping("/chat/message/{chatRoomId}")
    public void message(@DestinationVariable Long chatRoomId, @Payload ChatDto.ChatMessageDto message) {
        // roomid 따로 받을지 이렇게 할지 물어보기
        chatMessageService.message(chatRoomId, message);

    } // sender command
}