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
                .profileImageUrl(otherUser.getProfileImage().getImgUrl()) // 상대방 프로필 사진
                .roomNumber(chatRoom.getRoomNumber())
                .build();

        return chatRoomDto;
    }

    // 서버에 반영이 왜 안되지?
    // 본인 전체 채팅방 목록 반환
    public Slice<ChatDto.ChatRoomListDto> room(Long userId, Pageable pageable) {

        User findUser = userService.getUserId(userId);

        Slice <ChatDto.ChatRoomListDto> chatRoomSlice = chatRoomRepository.findAllByUserId(userId, pageable);

        return new SliceImpl<>(chatRoomSlice.getContent(), pageable, chatRoomSlice.hasNext());
    }


    // 특정 채팅방 조회
    public Slice <ChatDto.ChatRoomListDto> roomInfo(String roomName, User user, Pageable pageable) {

        userService.getUserId(user.getId());

        // 채팅방 이름으로 검색
        Slice <ChatDto.ChatRoomListDto> chatRoomSlice = chatRoomRepository.queryRoomsByName(roomName, user.getId(), pageable);

        // 채팅방 메시지로 검색
        Slice <ChatDto.ChatRoomListDto> chatRoomMessageSlice = chatRoomRepository.queryRoomsByMessage(roomName, user.getId(), pageable);

        // 두 결과를 합치기
        List<ChatDto.ChatRoomListDto> roomList = new ArrayList<>(chatRoomSlice.getContent());
        roomList.addAll(chatRoomMessageSlice.getContent());

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), roomList.size());
        List<ChatDto.ChatRoomListDto> pagedRoomList = roomList.subList(start, end);

        boolean hasNext = end < roomList.size();

        return new SliceImpl<>(pagedRoomList, pageable, hasNext);
    }

    // 대화 조회
    public List<ChatDto.ChatMessageResponseDto> loadMessage(Long roomId, User user) {

        User userOptional = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        List<Chat.ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdOrderByIdDesc(chatRoom.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

//        if (chatMessages.isEmpty()) {
//            return Collections.singletonList(
//                    ChatDto.ChatMessageResponseDto.builder()
//                            .message("채팅방에 메세지가 존재하지 않습니다.")
//                            .build()
//            );
//        }

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

//    public String getUnreadMessageCountsForUser(Long userId) {
//        List<Long> unreadMessageCount = chatRoomRepository.getUnreadMessageCountsForUser(userId);
//        return "읽지 않은 메세지 수 : " + unreadMessageCount + "개";
//    }
}
