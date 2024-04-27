package com.nawabali.nawabali.service;

//import com.amazonaws.services.ec2.model.GetReservedInstancesExchangeQuoteRequest;
//import com.nawabali.nawabali.constant.MessageType;
import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.global.websocket.WebSocketChatRoomCount;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static com.nawabali.nawabali.constant.ChatRoomEnum.GROUP;

@Service
@AllArgsConstructor
@Transactional
@Slf4j(topic = "ChatMessageService 메세지 보내는 로그")
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationService notificationService;
    private ObjectMapper objectMapper;
    private final WebSocketChatRoomCount chatRoomCount;
    private final WebSocketChatRoomCount webSocketChatRoomCount;

    // 입장
    public void enterMessage(Long chatRoomId, ChatDto.ChatMessageDto message, Principal principal) {

        User userOptional = userRepository.findByEmail(principal.getName())
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        log.info("본인인증" + userOptional);

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        log.debug("chatroom roomid 찾기" + chatRoom);

        if (!chatRoomRepository.findByIdAndUserId(chatRoom.getId(),userOptional.getId()).isPresent()){
            message.setMessage(userOptional.getNickname() + "님이 입장하셨습니다.");
            log.debug("메세지가 잘 들어오는지" + message);
            messagingTemplate.convertAndSend("/sub/chat/enter/message" + chatRoomId, message);

            Chat.ChatRoom chatRoomSave = Chat.ChatRoom.builder()
                    .roomName(chatRoom.getRoomName())
                    .roomNumber(chatRoom.getRoomNumber())
                    .user(userOptional)
                    .chatRoomEnum(GROUP)
                    .build();

            chatRoomRepository.save(chatRoomSave);
            log.debug("세이브 된 내용" + chatRoomSave);
        }

        // 읽지 않은 메세지 읽음 표시
//        List <Chat.ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomIdAndUserId(chatRoom.getId(), userOptional.getId())
//                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
//        log.debug("메세지 리스트" + chatMessageList);
//
//        for (Chat.ChatMessage user : chatMessageList) {
//            Chat.ChatMessage chatMessage = new Chat.ChatMessage();
//            chatMessage.update(true);
//            chatMessageRepository.save(chatMessage);
//            log.debug("메세지?!" + chatMessage);
//        }

        List<Chat.ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(chatRoom.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        log.info("받은 메세지" + chatMessageList);

        List <Chat.ChatMessage> chatMessageList1 = chatMessageRepository.findByChatRoomIdOrderByIdDesc(chatRoom.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        log.debug("메세지 리스트" + chatMessageList);

        for (Chat.ChatMessage chatMessage : chatMessageList) {
           if (!chatMessage.getUser().getId().equals(userOptional.getId())) {
               chatMessage.setReceiverRead(true);
               chatMessageRepository.save(chatMessage);
           }
        }

    }

    // 메세지 보내기
    public void message(Long chatRoomId, ChatDto.ChatMessageDto message, Principal principal) {

        User userOptional = userRepository.findByEmail(principal.getName())
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        log.info("본인인증" + userOptional);

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        log.info("방번호" + chatRoom);

        // 이걸로 현재 방에 있는 사람들 count로 받기
        int memberInRoom = chatRoomCount.getChatRoomUserCountInRoom(chatRoomId);
        log.info("현재 방에 있는 사람" + memberInRoom);

        String receiver = "";
        if (userOptional.getNickname().equals(chatRoom.getUser().getNickname())) {
            if (chatRoom.getOtherUser() != null) {
                receiver = chatRoom.getOtherUser().getNickname();
            }
        } else { receiver = chatRoom.getUser().getNickname();}

        if (memberInRoom == 2) {
            log.info("몇명?" +memberInRoom);

            Chat.ChatMessage sendMessage = Chat.ChatMessage.builder()
                    .sender(userOptional.getNickname())
                    .receiver(receiver)
                    .message(message.getMessage())
                    .createdMessageAt(LocalDateTime.now())
                    .isRead(true)
                    .isReceiverRead(true)
                    .user(userOptional)
                    .chatRoom(chatRoom)
                    .build();

            chatMessageRepository.save(sendMessage);
            log.info("저장" + sendMessage);

            ChatDto.ChatMessageResponseDto chatMessageResponseDto = ChatDto.ChatMessageResponseDto.builder()
                    .id(sendMessage.getId()) // 채팅 메세지 ID
                    .roomId(sendMessage.getChatRoom().getId())
                    .userId(sendMessage.getUser().getId())
                    .sender(sendMessage.getSender())
                    .message(sendMessage.getMessage())
                    .receiver(sendMessage.getReceiver())
                    .isRead(sendMessage.isRead())
                    .isReceiverRead(sendMessage.isReceiverRead())
                    .createdMessageAt(LocalDateTime.now())
                    .build();

            log.info("현재 채팅방에 " + memberInRoom + "명이 있는 채팅방에 메세지를 보내셨습니다" );
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, chatMessageResponseDto);
            log.info("정보확인 {} 이 방에서 새로운 메시지가 도착했습니다. 보낸 사람: {}, 메시지 내용: {}, 유저 아이디 : {}, 만든 시간 {}", chatRoomId, chatMessageResponseDto.getSender(), chatMessageResponseDto.getMessage(), chatMessageResponseDto.getUserId(), chatMessageResponseDto.getCreatedMessageAt());

        }

        if (memberInRoom == 1) {
            log.info("몇명?" +memberInRoom);
            Chat.ChatMessage sendMessage = Chat.ChatMessage.builder()
                    .sender(userOptional.getNickname())
                    .receiver(receiver)
                    .message(message.getMessage())
                    .createdMessageAt(LocalDateTime.now())
                    .isRead(true)
                    .isReceiverRead(false)
                    .user(userOptional)
                    .chatRoom(chatRoom)
                    .build();

            chatMessageRepository.save(sendMessage);
            log.info("저장" + sendMessage);

            ChatDto.ChatMessageResponseDto chatMessageResponseDto = ChatDto.ChatMessageResponseDto.builder()
                    .id(sendMessage.getId()) // 채팅 메세지 ID
                    .roomId(sendMessage.getChatRoom().getId())
                    .userId(sendMessage.getUser().getId())
                    .sender(sendMessage.getSender())
                    .message(sendMessage.getMessage())
                    .receiver(sendMessage.getReceiver())
                    .isRead(sendMessage.isRead())
                    .isReceiverRead(sendMessage.isReceiverRead())
                    .createdMessageAt(LocalDateTime.now())
                    .build();

            log.info("현재 채팅방에 {}명이 있는 채팅방에 메세지를 보내셨습니다" + memberInRoom);
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, chatMessageResponseDto);
            log.info("정보확인 {} 이 방에서 새로운 메시지가 도착했습니다. 보낸 사람: {}, 메시지 내용: {}, 유저 아이디 : {}, 만든 시간 {}", chatRoomId, chatMessageResponseDto.getSender(), chatMessageResponseDto.getMessage(), chatMessageResponseDto.getUserId(), chatMessageResponseDto.getCreatedMessageAt());

        }

        notificationService.notifyMessage(chatRoom.getRoomNumber(), receiver, userOptional.getNickname());

    }

}