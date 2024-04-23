package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.ChatDto;
//import com.nawabali.nawabali.dto.ChattingRequest;
import com.nawabali.nawabali.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Tag(name = "채팅 API", description = "채팅 관련 API 입니다.")
@RequiredArgsConstructor
@Controller
@Slf4j (topic = "채팅방 로그!!!")
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Operation(summary = "채팅방(DM) 입장" , description = "MessageType : ENTER _ 입장")
    @MessageMapping("/chat/enter/message/{chatRoomId}")
    public void enterMessage(@DestinationVariable Long chatRoomId, ChatDto.ChatMessageDto message, Principal principal) {

        chatMessageService.enterMessage(chatRoomId, message, principal);

    }

    @Operation(summary = "채팅(DM) 전송" , description = "MessageType : TALK _ 메세지 전송")
    @MessageMapping("/chat/message/{chatRoomId}")
    public void message(@DestinationVariable Long chatRoomId, @Payload ChatDto.ChatMessageDto message, Principal principal) {
        log.info("principal " + principal);
        // roomid 따로 받을지 이렇게 할지 물어보기
        chatMessageService.message(chatRoomId, message, principal);
        log.info("값 들어가지?" + principal);
        log.info("정보확인 {} 이 방에서 {} 이 반환값", chatRoomId, message.getMessage());

    } // sender command

//    @MessageMapping ("/chat/message/{chatRoomId}")
//    public void chat (@DestinationVariable Long chatRoomId, ChatDto.ChatMessageDto message) {
//        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, message.getMessage());
//        log.info("Message [{}] send by member : {} to chatting room:{}",message.getMessage(), message.getUserId(), chatRoomId);
//    }



}