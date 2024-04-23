package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.ProfileImageRepository;
import com.nawabali.nawabali.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ProfileImageRepository profileImageRepository;

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
                .profileImageId(otherUser.getProfileImage().getId()) // 상대방 프로필 사진
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
    public Slice <ChatDto.ChatRoomListDto> roomInfo(String roomName, User user, Pageable pageable) {

        userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));
//        log.info("아이디" + user.getId());
//        Slice <ChatDto.ChatRoomListDto> chatRoomListDtoSlice = chatRoomRepository.findChatRoomByRoomName(roomName, pageable);
//        log.info("결과?" + chatRoomListDtoSlice);
        List<Chat.ChatRoom> chatRooms = chatRoomRepository.findByRoomNameContainingIgnoreCase(roomName)
                .orElseThrow(()-> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        List<LocalDateTime> messageCreationDates = new ArrayList<>(); // 이게 아닌듯?
        for (Chat.ChatRoom chatRoom : chatRooms) {
            chatRoom.getLatestMessage().ifPresent(chatMessage -> {
                messageCreationDates.add(chatMessage.getCreatedMessageAt());
            });
        }

        // ID에 따라 내림차순으로 정렬
        chatRooms.sort(Comparator.comparing(Chat.ChatRoom::getId).reversed());

        Slice <ChatDto.ChatRoomListDto> slice = new SliceImpl<>(chatRooms.stream()
                .map(chatRoom -> ChatDto.ChatRoomListDto.builder()
                        .roomId(chatRoom.getId())
                        .roomNumber(chatRoom.getRoomNumber())
                        .roomName(chatRoom.getRoomName())
                        .build())
                .collect(Collectors.toList()));

        return slice;
//        return new SliceImpl<>(chatRoomListDtoSlice.getContent(), pageable, chatRoomListDtoSlice.hasNext());
    }

    // 대화 조회
    public List<ChatDto.ChatMessageResponseDto> loadMessage(Long roomId, User user) {

        User userOptional = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        List<Chat.ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        if (chatMessages.isEmpty()) {
            return Collections.singletonList(
                    ChatDto.ChatMessageResponseDto.builder()
                            .message("채팅방에 메세지가 존재하지 않습니다.")
                            .build()
            );
        }

        // ChatMessage를 ChatDto.ChatMessage로 변환하여 반환
        return chatMessages.stream()
                .map(chatMessage -> ChatDto.ChatMessageResponseDto.builder()
                        .id(chatMessage.getId())
//                        .type(chatMessage.getType())
                        .sender(chatMessage.getSender())
                        .message(chatMessage.getMessage())
                        .createdMessageAt(chatMessage.getCreatedMessageAt())
                        .build())
                .collect(Collectors.toList());
    }

}
