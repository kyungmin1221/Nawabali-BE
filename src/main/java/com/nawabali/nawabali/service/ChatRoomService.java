package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.nawabali.nawabali.constant.ChatRoomEnum.GROUP;
import static com.nawabali.nawabali.constant.ChatRoomEnum.PERSONAL;

@Service
@AllArgsConstructor
@Transactional
@Slf4j(topic = "ChatService 로그")
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;

    // 채팅방 생성
    public ChatDto.ChatRoomDto createRoom(ChatRoomEnum chatRoomEnum, String roomName, User user) {

        User findUser = userService.getUserId(user.getId());

        User otherUser = new User();

        Chat.ChatRoom chatRoom = null;

        if (chatRoomEnum.equals(ChatRoomEnum.GROUP)) {

            chatRoom = Chat.ChatRoom.builder()
                    .chatRoomEnum(ChatRoomEnum.GROUP)
                    .roomName(roomName)
                    .user(user)
                    .roomNumber(UUID.randomUUID().toString())
                    .build();
        }

        if (chatRoomEnum.equals(PERSONAL)) {

            otherUser = userRepository.findByNickname(roomName);

            Optional <Chat.ChatRoom> existChatRoom = chatRoomRepository.findByUserIdAndOtherUserId(findUser.getId(), otherUser.getId());

            if (otherUser == null || otherUser == findUser) {
                throw new CustomException(ErrorCode.WRONG_OTHERUSER);
            }

            if (existChatRoom.isPresent()) {
                throw new CustomException(ErrorCode.DUPLICATE_CHATROOM);
            }

            chatRoom = Chat.ChatRoom.builder()
                    .chatRoomEnum(PERSONAL)
                    .user(user)
                    .otherUser(otherUser)
                    .roomName("SecretChatRoom")
                    .roomNumber(UUID.randomUUID().toString())
                    .build();

        }

        chatRoomRepository.save(chatRoom);

        ChatDto.ChatRoomDto chatRoomDto = ChatDto.ChatRoomDto.builder()
                .roomId(chatRoom.getId())
                .chatRoomEnum(chatRoomEnum)
                .roomName(roomName)
                .userId(user.getId())
                .otherUserId(otherUser.getId())
                .roomNumber(chatRoom.getRoomNumber())
                .build();

        return chatRoomDto;
    }

    // 본인 전체 채팅방 목록 반환
    public Slice<ChatDto.ChatRoomListDto> room(Long userId, Pageable pageable) {

        User findUser = userService.getUserId(userId);

        Slice <ChatDto.ChatRoomListDto> chatRoomSlice = chatRoomRepository.findAllByUserId(userId, pageable);

        return new SliceImpl<>(chatRoomSlice.getContent(), pageable, chatRoomSlice.hasNext());
    }


    // 특정 채팅방 조회
    public List<ChatDto.ChatRoomDto> roomInfo(String roomName, User user) {

        userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        List<Chat.ChatRoom> chatRooms = chatRoomRepository.findByRoomNameContainingIgnoreCase(roomName)
                .orElseThrow(()-> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        // ID에 따라 내림차순으로 정렬
        chatRooms.sort(Comparator.comparing(Chat.ChatRoom::getId).reversed());

        return chatRooms.stream()
                .map(chatRoom -> ChatDto.ChatRoomDto.builder()
                        .roomId(chatRoom.getId())
                        .roomNumber(chatRoom.getRoomNumber())
                        .roomName(chatRoom.getRoomName())
                        .build())
                .collect(Collectors.toList());
    }

    // 대화 조회
    public List<ChatDto.ChatMessageDto> loadMessage(Long roomId, User user) {

        User userOptional = userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        List<Chat.ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        // ChatMessage를 ChatDto.ChatMessage로 변환하여 반환
        return chatMessages.stream()
                .map(chatMessage -> ChatDto.ChatMessageDto.builder()
                        .id(chatMessage.getId())
                        .type(chatMessage.getType())
                        .sender(chatMessage.getSender())
                        .message(chatMessage.getMessage())
                        .createdAt(chatMessage.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

}
