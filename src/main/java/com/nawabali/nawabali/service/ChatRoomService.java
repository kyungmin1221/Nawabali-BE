package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.domain.Chat.ChatMessage;
import com.nawabali.nawabali.domain.Chat.ChatRoom;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.dto.ChatDto.ChatMessageResponseDto;
import com.nawabali.nawabali.dto.ChatDto.ChatRoomDto;
import com.nawabali.nawabali.dto.ChatDto.ChatRoomListResponseDto;
import com.nawabali.nawabali.dto.ChatDto.ChatRoomSearchListDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nawabali.nawabali.constant.ChatRoomEnum.PERSONAL;

@Service
@AllArgsConstructor
@Transactional
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatDto.ChatRoomDto createRoom (ChatRoomEnum chatRoomEnum, String roomName, User user) {

        User findUser = getUserId(user.getId());
        User otherUser = userRepository.findByNickname(roomName);
        if (otherUser == null || otherUser == findUser) {
            throw new CustomException(ErrorCode.WRONG_OTHERUSER);}
        ChatRoom chatRoom = null;

        if (chatRoomEnum.equals(PERSONAL)) {

            Optional <ChatRoom> existChatRoom = chatRoomRepository.findByUserIdAndOtherUserId(findUser.getId(), otherUser.getId());
            if (existChatRoom.isPresent()) {
                throw new CustomException(ErrorCode.DUPLICATE_CHATROOM);
            }

            chatRoom = ChatRoom.builder()
                    .chatRoomEnum(PERSONAL)
                    .roomName("SecretChatRoom")
                    .user(user)
                    .otherUser(otherUser)
                    .build();
            chatRoomRepository.save(chatRoom);
        }

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .roomId(chatRoom.getId())
                .chatRoomEnum(chatRoomEnum)
                .roomName(roomName)
                .userId(user.getId())
                .otherUserId(otherUser.getId())
                .profileImageUrl(otherUser.getProfileImage().getImgUrl()) // 상대방 프로필 사진
                .build();
        return chatRoomDto;
    }

    public Slice<ChatRoomListResponseDto> room (User user, Pageable pageable) {
        getUserId(user.getId());
        Slice <ChatRoomListResponseDto> chatRoomSlice = chatRoomRepository.findAllByUserId(user.getId(), pageable);
        return new SliceImpl<>(chatRoomSlice.getContent(), pageable, chatRoomSlice.hasNext());
    }

    public Slice <ChatRoomSearchListDto> roomInfo (String roomName, User user, Pageable pageable) {

        getUserId(user.getId());

        Slice <ChatRoomSearchListDto> chatRoomSlice = chatRoomRepository.queryRoomsByName(roomName, user.getId(), pageable);
        Slice <ChatRoomSearchListDto> chatRoomMessageSlice = chatRoomRepository.queryRoomsByMessage(roomName, user.getId(), pageable);

        List<ChatRoomSearchListDto> roomList = new ArrayList<>(chatRoomSlice.getContent());
        roomList.addAll(chatRoomMessageSlice.getContent());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), roomList.size());
        List<ChatRoomSearchListDto> pagedRoomList = roomList.subList(start, end);
        boolean hasNext = end < roomList.size();

        return new SliceImpl<>(pagedRoomList, pageable, hasNext);
    }

    public Slice<ChatMessageResponseDto> loadMessage (Long roomId, User user, Pageable pageable) {

        getUserId(user.getId());

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        Slice<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdOrderByIdDesc(chatRoom.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        return chatMessages.map(chatMessage -> ChatMessageResponseDto.builder()
                        .id(chatMessage.getId())
                        .sender(chatMessage.getSender())
                        .message(chatMessage.getMessage())
                        .createdMessageAt(chatMessage.getCreatedMessageAt())
                        .roomId(chatMessage.getChatRoom().getId())
                        .userId(chatMessage.getUser().getId())
                        .receiver(chatMessage.getReceiver())
                        .isRead(chatMessage.isRead())
                        .isReceiverRead(chatMessage.isReceiverRead())
                        .build());
    }

    public User getUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
