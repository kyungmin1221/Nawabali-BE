package com.nawabali.nawabali.service;

//import com.amazonaws.services.ec2.model.GetReservedInstancesExchangeQuoteRequest;
//import com.nawabali.nawabali.constant.MessageType;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.nawabali.nawabali.constant.ChatRoomEnum.GROUP;

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
        log.debug("유저 인포" + userOptional);

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        log.debug("chatroom roomid 찾기" + chatRoom);

        if (!chatRoomRepository.findByIdAndUserId(chatRoom.getId(),userOptional.getId()).isPresent()){
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
            log.debug("메세지가 잘 들어오는지" + message);
            messagingTemplate.convertAndSend("/chat/enter/message" + message.getRoomId(), message);

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
        List <Chat.ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomIdAndUserId(chatRoom.getId(), userOptional.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        log.debug("메세지 리스트" + chatMessageList);

        for (Chat.ChatMessage user : chatMessageList) {
            Chat.ChatMessage chatMessage = new Chat.ChatMessage();
            chatMessage.update(true);
            chatMessageRepository.save(chatMessage);
            log.debug("메세지?!" + chatMessage);
        }

    }

    // 메세지 보내기
    public List <ChatDto.ChatMessageResponseDto> message(ChatDto.ChatMessageDto message) {

        User userOptional = userRepository.findById(message.getUserId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        log.info("userOptional 유저 인포메이션" + userOptional);

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        log.info("chatroom roomid 찾기" + chatRoom);

        List<User> usersInChatRoom;

        Object chatRoomUsers = chatRoom.getUser();
        log.debug("채팅방에 있는 사람들 가져오기" + chatRoomUsers);

        if (chatRoomUsers instanceof List) {
            usersInChatRoom = (List<User>) chatRoomUsers;
            log.debug("채팅방에 혼자 있을때" + usersInChatRoom);
        } else if (chatRoomUsers instanceof User) {
            usersInChatRoom = Collections.singletonList((User) chatRoomUsers);
            log.debug("채팅방에 여러명 있을때" + usersInChatRoom);
        } else {
            throw new IllegalStateException("반환한 객체의 타입이 예상과 다릅니다" + chatRoomUsers.getClass());
        }

        List <ChatDto.ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();

        for (User user : usersInChatRoom) {
            log.info("가져온 유저들의 정보 보내기 위해서 한명씩 꺼내기" + user);
            Chat.ChatMessage allUser = Chat.ChatMessage.builder()
                    .sender(userOptional.getNickname())
                    .receiver(user.getNickname())
                    .message(message.getMessage())
                    .createdMessageAt(LocalDateTime.now())
                    .isRead(user.equals(userOptional))
                    .user(user)
                    .chatRoom(chatRoom)
                    .build();

            chatMessageRepository.save(allUser);
            log.info("유저 정보 세이브 한거" + allUser);

            ChatDto.ChatMessageResponseDto chatMessageResponseDto = ChatDto.ChatMessageResponseDto.builder()
                    .id(allUser.getId()) // 채팅 메세지 ID
                    .roomId(allUser.getChatRoom().getId())
                    .userId(allUser.getUser().getId())
                    .sender(allUser.getSender())
                    .message(allUser.getMessage())
                    .receiver(allUser.getReceiver())
                    .isRead(user.equals(userOptional))
                    .createdMessageAt(LocalDateTime.now())
                    .build();

            chatMessageResponseDtoList.add(chatMessageResponseDto);
        }

//        notificationService.notifyMessage(chatRoom.getRoomNumber(), message.getUserId(), message.getSender());

        messagingTemplate.convertAndSend("/chat/message" + message.getRoomId(), message);
        messagingTemplate.convertAndSend("/chat/message" + message.getRoomId(), chatMessageResponseDtoList);
        log.info("메세지 보내는거?" + messagingTemplate);

        return chatMessageResponseDtoList;
    }
}