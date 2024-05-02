package com.nawabali.nawabali.service;

import com.nawabali.nawabali.domain.Chat.ChatMessage;
import com.nawabali.nawabali.domain.Chat.ChatRoom;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.dto.ChatDto.ChatMessageResponseDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.global.websocket.WebSocketChatRoomCount;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class ChatMessageService {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WebSocketChatRoomCount chatRoomCount;
    private final SimpMessageSendingOperations messagingTemplate;

    public void message(Long chatRoomId, ChatDto.ChatMessageDto message, Principal principal) {

        User userOptional = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        int memberInRoom = chatRoomCount.getChatRoomUserCountInRoom(chatRoomId);

        if (message.getType().equals(ChatMessage.MessageType.ENTER)) {

            List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomIdAndIsReceiverReadFalse(chatRoom.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

            for (ChatMessage chatMessages : chatMessageList) {
                if (!chatMessages.getUser().getId().equals(userOptional.getId())) {
                    chatMessages.receiverRead(true);
                    chatMessageRepository.save(chatMessages);
                }
            }
            notificationService.deleteAllNotification(userOptional,chatRoomId);
            notificationService.notifyAllMyMessage(userOptional.getNickname());
            return;
        }

        if (message.getType().equals(ChatMessage.MessageType.TALK)) {

            String receiver = "";
            if (userOptional.getNickname().equals(chatRoom.getUser().getNickname())) {
                if (chatRoom.getOtherUser() != null) {
                    receiver = chatRoom.getOtherUser().getNickname();
                }
            } else {
                receiver = chatRoom.getUser().getNickname();
            }

            if (memberInRoom == 2) {
                ChatMessage sendMessage = ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .user(userOptional)
                        .sender(userOptional.getNickname())
                        .isRead(true)
                        .message(message.getMessage())
                        .createdMessageAt(LocalDateTime.now())
                        .receiver(receiver)
                        .isReceiverRead(true)
                        .build();
                chatMessageRepository.save(sendMessage);

                ChatMessageResponseDto chatMessageResponseDto = ChatMessageResponseDto.builder()
                        .id(sendMessage.getId())
                        .roomId(sendMessage.getChatRoom().getId())
                        .userId(sendMessage.getUser().getId())
                        .sender(sendMessage.getSender())
                        .isRead(sendMessage.isRead())
                        .message(sendMessage.getMessage())
                        .createdMessageAt(LocalDateTime.now())
                        .receiver(sendMessage.getReceiver())
                        .isReceiverRead(sendMessage.isReceiverRead())
                        .build();
                messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, chatMessageResponseDto);
            }

            if (memberInRoom == 1) {
                ChatMessage sendMessage = ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .user(userOptional)
                        .sender(userOptional.getNickname())
                        .isRead(true)
                        .message(message.getMessage())
                        .createdMessageAt(LocalDateTime.now())
                        .receiver(receiver)
                        .isReceiverRead(false)
                        .build();
                chatMessageRepository.save(sendMessage);
                log.info("저장확인" + sendMessage);
                log.info("현재시간" + LocalDateTime.now());

                ChatMessageResponseDto chatMessageResponseDto = ChatMessageResponseDto.builder()
                        .id(sendMessage.getId())
                        .roomId(sendMessage.getChatRoom().getId())
                        .userId(sendMessage.getUser().getId())
                        .sender(sendMessage.getSender())
                        .isRead(sendMessage.isRead())
                        .message(sendMessage.getMessage())
                        .createdMessageAt(LocalDateTime.now())
                        .receiver(sendMessage.getReceiver())
                        .isReceiverRead(sendMessage.isReceiverRead())
                        .build();

                messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, chatMessageResponseDto);
                notificationService.notifyMessage(chatRoom.getId(), receiver, userOptional.getNickname());
            }

            notificationService.notifyAllMyMessage(userOptional.getNickname());
            notificationService.notifyAllYourMessage(receiver);
        }
    }
}