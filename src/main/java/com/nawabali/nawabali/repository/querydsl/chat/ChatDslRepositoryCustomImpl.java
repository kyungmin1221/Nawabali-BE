package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.QChat_ChatMessage;
import com.nawabali.nawabali.domain.QChat_ChatRoom;
import com.nawabali.nawabali.domain.QUser;
import com.nawabali.nawabali.domain.image.QProfileImage;
import com.nawabali.nawabali.dto.ChatDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ChatDslRepositoryCustomImpl implements ChatDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List <ChatDto.ChatRoomListResponseDto> findAllByUserId (Long userId){

        QUser user = QUser.user;
        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;
        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;

        List <Chat.ChatRoom> chatRooms = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.user, user).fetchJoin()
                .leftJoin(chatRoom.otherUser).fetchJoin()
                .where(user.id.eq(userId)
                        .or(chatRoom.otherUser.id.eq(userId)))
                .orderBy(chatRoom.Id.desc())
                .fetch();

        List <ChatDto.ChatRoomListResponseDto> chatRoomDtos = chatRooms.stream()
                .map(newchatRoom -> {

                    if (newchatRoom.getChatRoomEnum().equals(ChatRoomEnum.PERSONAL)) {

                        ChatDto.MessageInfo messageInfo = info(newchatRoom, userId);

                        String roomName = messageInfo.getRoomName();
                        String profileImage = messageInfo.getProfileImageUrl();
                        Long unreadCount = messageInfo.getUnreadCount();

                        List<Chat.ChatMessage> latestMessages = queryFactory
                                .selectFrom(chatMessage)
                                .where(chatMessage.chatRoom.Id.eq(newchatRoom.getId()))
                                .orderBy(chatMessage.createdMessageAt.desc())
                                .limit(1)
                                .fetch();

                        String latestMessageContent = "";
                        if (!latestMessages.isEmpty()) {
                            latestMessageContent = latestMessages.get(0).getMessage();
                        }

                        return ChatDto.ChatRoomListResponseDto.builder()
                                .roomId(newchatRoom.getId())
                                .roomName(roomName)
                                .chatMessage(latestMessageContent)
                                .profileImageUrl(profileImage)
                                .unreadCount(unreadCount)
                                .build();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        return chatRoomDtos;
    }


    public List <ChatDto.ChatRoomSearchListDto> queryRoomsByName(String roomName, Long userId) {

        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;
        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;
        QProfileImage profileImage = QProfileImage.profileImage;

        List<Chat.ChatRoom> chatRooms = queryFactory.selectFrom(chatRoom)
                .leftJoin(chatRoom.user.profileImage, profileImage)
                .leftJoin(chatRoom.chatMessageList, chatMessage)
                .where(chatRoom.user.id.eq(userId).and(chatRoom.roomName.contains(roomName))
                                .or(chatRoom.user.id.eq(Long.valueOf(userId)).and(chatRoom.otherUser.nickname.contains(roomName)))
                                .or(chatRoom.otherUser.isNotNull().and(chatRoom.otherUser.id.eq(Long.valueOf(userId))).and(chatRoom.user.nickname.contains(roomName))))
                .orderBy(chatMessage.createdMessageAt.desc())
                .fetch();

        List<ChatDto.ChatRoomSearchListDto> chatRoomss = new ArrayList<>();

        for (Chat.ChatRoom chatRoomEntity : chatRooms) {

            ChatDto.MessageInfo messageInfo = info(chatRoomEntity, userId);
            String roomNameDto = messageInfo.getRoomName();

            ChatDto.ChatRoomSearchListDto chatRoomDto = ChatDto.ChatRoomSearchListDto.builder()
                    .profileImageUrl(messageInfo.getProfileImageUrl())
                    .roomName(roomNameDto)
                    .chatMessage(chatRoomEntity.getLatestMessage().map(Chat.ChatMessage::getMessage).orElse(""))
                    .notice("***** 채팅방 검색 결과")
                    .build();
            chatRoomss.add(chatRoomDto);
        }
        return chatRoomss;
    }

    public List <ChatDto.ChatRoomSearchListDto> queryRoomsByMessage(String roomName, Long userId) {

        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;
        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;

        List<Chat.ChatMessage> chatMessageList = queryFactory.selectFrom(chatMessage)
                .leftJoin(chatMessage.chatRoom, chatRoom)
                .where(chatMessage.message.contains(roomName)
                        .and(chatRoom.user.id.eq(userId)
                        .or(chatRoom.otherUser.id.eq(userId))))
                .orderBy(chatMessage.createdMessageAt.desc())
                .fetch();

        List<ChatDto.ChatRoomSearchListDto> chatMessages = new ArrayList<>();

        for (Chat.ChatMessage chatMessageEntity : chatMessageList) {

            ChatDto.MessageInfo messageInfo = info(chatMessageEntity.getChatRoom(), userId);
            String roomNameDto = messageInfo.getRoomName();

            ChatDto.ChatRoomSearchListDto chatRoomDto = ChatDto.ChatRoomSearchListDto.builder()
                    .profileImageUrl(messageInfo.getProfileImageUrl())
                    .roomName(roomNameDto)
                    .chatMessage(chatMessageEntity.getMessage())
                    .notice("::::: 채팅메세지 검색 결과")
                    .build();
            chatMessages.add(chatRoomDto);
        }
        return chatMessages;
    }

    public Long getUnreadMessageCountsForUser (String userName) {

        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;
        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;

        long totalUnreadCount = 0;

        List<Long> receiverUnreadCounts = queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom)
                .where(chatMessage.receiver.eq(userName)
                        .and(chatMessage.isReceiverRead.eq(false)))
                .fetch();

        for (Long count : receiverUnreadCounts) {
            totalUnreadCount += count;}
        return totalUnreadCount;
    }

    private ChatDto.MessageInfo info (Chat.ChatRoom chatRoom, Long userId) {

        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;

        String roomName = "";
        String profileImage = "";
        Long unreadcount = 0L;

        if (userId.equals(chatRoom.getUser().getId())) {
            roomName = chatRoom.getOtherUser().getNickname();
            profileImage = chatRoom.getOtherUser().getProfileImage().getImgUrl();
            unreadcount = queryFactory
                    .select(chatMessage.count())
                    .from(chatMessage)
                    .where(chatMessage.chatRoom.Id.eq(chatRoom.getId())
                            .and(chatMessage.isReceiverRead.eq(false))
                            .and(chatMessage.receiver.eq(chatRoom.getUser().getNickname())))
                    .fetchOne();
        }

        if (userId.equals(chatRoom.getOtherUser().getId())){
            roomName = chatRoom.getUser().getNickname();
            profileImage = chatRoom.getUser().getProfileImage().getImgUrl();
            unreadcount = queryFactory
                    .select(chatMessage.count())
                    .from(chatMessage)
                    .where(chatMessage.chatRoom.Id.eq(chatRoom.getId())
                            .and(chatMessage.isReceiverRead.eq(false))
                            .and(chatMessage.receiver.eq(chatRoom.getOtherUser().getNickname())))
                    .fetchOne();
        }

        ChatDto.MessageInfo info = ChatDto.MessageInfo.builder()
                        .roomName(roomName)
                        .profileImageUrl(profileImage)
                        .unreadCount(unreadcount)
                        .build();
        return info;
    }
}
