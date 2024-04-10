package com.nawabali.nawabali.service;

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
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j(topic = "ChatService 로그")
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 생성
    public ChatDto.ChatRoomDto createRoom(String name, User user) {

        userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        Chat.ChatRoom chatRoom = Chat.ChatRoom.builder()
                .roomNumber(UUID.randomUUID().toString())
                .name(name)
                .build();
        chatRoomRepository.save(chatRoom);

        ChatDto.ChatRoomDto chatRoomDto = ChatDto.ChatRoomDto.builder()
                .roomId(chatRoom.getId())
                .roomNumber(chatRoom.getRoomNumber())
                .name(name)
                .build();

        return chatRoomDto;
    }

    // 본인 전체 채팅방 목록 반환
    public List<ChatDto.ChatRoomDto> room(User user) {

        userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        List<Chat.ChatRoom> chatRooms = chatRoomRepository.findAllByUserId(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.INCHATROOM_NOT_FOUND));

        Collections.reverse(chatRooms);
        return chatRooms.stream()
                .map(chatRoom -> ChatDto.ChatRoomDto.builder()
                        .roomId(chatRoom.getId())
                        .roomNumber(chatRoom.getRoomNumber())
                        .name(chatRoom.getName())
                        .build())
                .collect(Collectors.toList());
    }


    // 특정 채팅방 조회
    public List<ChatDto.ChatRoomDto> roomInfo(String name, User user) {

        userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        List<Chat.ChatRoom> chatRooms = chatRoomRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(()-> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        // ID에 따라 내림차순으로 정렬
        chatRooms.sort(Comparator.comparing(Chat.ChatRoom::getId).reversed());

        return chatRooms.stream()
                .map(chatRoom -> ChatDto.ChatRoomDto.builder()
                        .roomId(chatRoom.getId())
                        .roomNumber(chatRoom.getRoomNumber())
                        .name(chatRoom.getName())
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
