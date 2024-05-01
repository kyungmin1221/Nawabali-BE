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

        List<Chat.ChatRoom> chatRooms = chatRoomRepository.findByRoomNameContainingIgnoreCase(roomName)
                .orElseThrow(()-> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        List<LocalDateTime> messageCreationDates = new ArrayList<>(); // 이게 아닌듯?
        for (Chat.ChatRoom chatRoom : chatRooms) {
            chatRoom.getLatestMessage().ifPresent(chatMessage -> {
                messageCreationDates.add(chatMessage.getCreatedMessageAt());
            });
        }

        // 최신 메시지 개수를 제한하고, 해당 메시지에서 검색어를 포함하는 채팅방만 필터링합니다.
        List<Chat.ChatRoom> filteredChatRooms = chatRooms.stream()
                .filter(chatRoom -> {
                    List<Chat.ChatMessage> latestMessages = chatRoom.getChatMessageList().stream()
                            .sorted(Comparator.comparing(Chat.ChatMessage::getCreatedMessageAt).reversed())
                            .limit(10)
                            .collect(Collectors.toList());
                    return latestMessages.stream()
                            .anyMatch(message -> message.getMessage().contains(roomName));
                })
                .collect(Collectors.toList());

        // 채팅방을 최신 메시지 생성일을 기준으로 내림차순으로 정렬합니다.
        filteredChatRooms.sort((room1, room2) -> {
            LocalDateTime latestMessageDate1 = room1.getLatestMessage().map(Chat.ChatMessage::getCreatedMessageAt).orElse(LocalDateTime.MIN);
            LocalDateTime latestMessageDate2 = room2.getLatestMessage().map(Chat.ChatMessage::getCreatedMessageAt).orElse(LocalDateTime.MIN);
            return latestMessageDate2.compareTo(latestMessageDate1);
        });

        // ID에 따라 내림차순으로 정렬
        chatRooms.sort(Comparator.comparing(Chat.ChatRoom::getId).reversed());

        Slice <ChatDto.ChatRoomListDto> slice = new SliceImpl<>(chatRooms.stream()
                .map(chatRoom -> ChatDto.ChatRoomListDto.builder()
                        .roomId(chatRoom.getId())
                        .roomName(chatRoom.getRoomName())
                        .build())
                .collect(Collectors.toList()));

        return slice;
    }

    // 대화 조회
    public List<ChatDto.ChatMessageResponseDto> loadMessage(Long roomId, User user) {

        User userOptional = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        List<Chat.ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdOrderByIdDesc(chatRoom.getId())
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
                        .roomId(chatMessage.getChatRoom().getId())
                        .userId(chatMessage.getUser().getId())
                        .receiver(chatMessage.getReceiver())
                        .isRead(chatMessage.isRead())
                        .isReceiverRead(chatMessage.isReceiverRead())
                        .build())
                .collect(Collectors.toList());
    }

}
