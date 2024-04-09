package com.nawabali.nawabali.service;

import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
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

    // 전체 채팅방 목록 반환
    public List<ChatDto.ChatRoomDto> room(User user) {

        userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        List<Chat.ChatRoom> chatRooms = chatRoomRepository.findAll();
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

}
