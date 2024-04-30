package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.QChat_ChatMessage;
import com.nawabali.nawabali.domain.QChat_ChatRoom;
import com.nawabali.nawabali.domain.QUser;
import com.nawabali.nawabali.domain.image.QProfileImage;
import com.nawabali.nawabali.dto.ChatDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.nawabali.nawabali.constant.ChatRoomEnum.GROUP;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatDslRepositoryCustomImpl implements ChatDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice <ChatDto.ChatRoomListDto> findAllByUserId (Long userId, Pageable pageable){

        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;
        QUser user = QUser.user;
        QUser otherUser = new QUser("otherUser");
        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;

        List <Chat.ChatRoom> chatRooms = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.user, user).fetchJoin()
                .leftJoin(chatRoom.otherUser).fetchJoin()
                .where(user.id.eq(userId)
                        .or(chatRoom.otherUser.id.eq(userId)))
                .orderBy(chatRoom.Id.desc())
                .offset(pageable.getOffset())
                .limit (pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = chatRooms.size() > pageable.getPageSize();

        if (hasNext) {
            chatRooms.remove(chatRooms.size() -1);
        }

        List <ChatDto.ChatRoomListDto> chatRoomDtos = chatRooms.stream()
                .map(newchatRoom -> {

                    if (newchatRoom.getChatRoomEnum().equals(ChatRoomEnum.PERSONAL)) {
                        String roomName = "";
                        String profileImage = "";
                        Long unreadcount = 0L;
                        // receiver 일때
                        if (userId.equals(newchatRoom.getUser().getId())) {
                            roomName = newchatRoom.getOtherUser().getNickname();
                            profileImage = newchatRoom.getOtherUser().getProfileImage().getImgUrl();
                            unreadcount = queryFactory
                                    .select(chatMessage.count())
                                    .from(chatMessage)
                                    .where(chatMessage.chatRoom.Id.eq(newchatRoom.getId())
                                            .and(chatMessage.isReceiverRead.eq(false))
                                            .and(chatMessage.receiver.eq(newchatRoom.getUser().getNickname())))
                                    .fetchOne();
                        } // sender일때
                        if (userId.equals(newchatRoom.getOtherUser().getId())){
                            roomName = newchatRoom.getUser().getNickname();
                            profileImage = newchatRoom.getUser().getProfileImage().getImgUrl();
                            unreadcount = queryFactory
                                    .select(chatMessage.count())
                                    .from(chatMessage)
                                    .where(chatMessage.chatRoom.Id.eq(newchatRoom.getId())
                                            .and(chatMessage.isReceiverRead.eq(false))
                                            .and(chatMessage.receiver.eq(newchatRoom.getOtherUser().getNickname())))
                                    .fetchOne();
                        }

                        // 여기서 가장 최신 메시지를 가져오기
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

                        return ChatDto.ChatRoomListDto.builder()
                                .roomId(newchatRoom.getId())
//                                .chatRoomEnum(newchatRoom.getChatRoomEnum())
                                .roomName(roomName)
//                                .roomNumber(newchatRoom.getRoomNumber())
                                .chatMessage(latestMessageContent)
                                .profileImageUrl(profileImage)
                                .unreadCount(unreadcount)
                                .build();
                    }

                    if (newchatRoom.getChatRoomEnum().equals(GROUP)){
                        return ChatDto.ChatRoomListDto.builder()
                                .roomId(newchatRoom.getId())
                                .chatRoomEnum(newchatRoom.getChatRoomEnum())
                                .roomName(newchatRoom.getRoomName())
                                .roomNumber(newchatRoom.getRoomNumber())
                                .build();
                    }

                    return null;

                })

                .collect(Collectors.toList());

        return new SliceImpl<>(chatRoomDtos, pageable, hasNext);

    }


    // 채팅방 이름으로 검색하는 메소드
    public Slice<ChatDto.chatRoomSearchListDto> queryRoomsByName(String roomName, Long userId, Pageable pageable) {

        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;
        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;
        QProfileImage profileImage = QProfileImage.profileImage;

        List<Chat.ChatRoom> chatRooms = queryFactory.selectFrom(chatRoom)
                .leftJoin(chatRoom.user.profileImage, profileImage)
                .leftJoin(chatRoom.chatMessageList, chatMessage)
                .where(
                        chatRoom.user.id.eq(userId).and(chatRoom.roomName.contains(roomName))
                                .or(chatRoom.user.id.eq(Long.valueOf(userId)).and(chatRoom.otherUser.nickname.contains(roomName)))
                                .or(chatRoom.otherUser.isNotNull().and(chatRoom.otherUser.id.eq(Long.valueOf(userId))).and(chatRoom.user.nickname.contains(roomName))))
                .orderBy(chatMessage.createdMessageAt.desc()) // 최신 메시지 순으로 정렬
                .fetch();

        List<ChatDto.chatRoomSearchListDto> chatRoomss = new ArrayList<>(); // 수정된 부분: chatRoomss를 초기화합니다.


        for (Chat.ChatRoom chatRoomEntity : chatRooms) {
            String roomNameDto = "";
            String profileImageUrl = "";
            if (userId.equals(chatRoomEntity.getUser().getId())) {
                roomNameDto = chatRoomEntity.getOtherUser().getNickname();
                profileImageUrl = chatRoomEntity.getOtherUser().getProfileImage().getImgUrl();
            }
            if (userId.equals(chatRoomEntity.getOtherUser().getId())){
                roomNameDto = chatRoomEntity.getUser().getNickname();
                profileImageUrl = chatRoomEntity.getUser().getProfileImage().getImgUrl();
            }
            Optional<Chat.ChatMessage> latestMessage = chatRoomEntity.getLatestMessage();
            Long messageId = null;
            if (latestMessage.isPresent()) {
                messageId = latestMessage.get().getId(); // messageId 가져오기
            }
            ChatDto.chatRoomSearchListDto chatRoomDto = ChatDto.chatRoomSearchListDto.builder()
                    .roomName(roomNameDto)
                    .chatMessage(chatRoomEntity.getLatestMessage().map(Chat.ChatMessage::getMessage).orElse(""))
                    .notice("***** 채팅방 검색 결과")
                    .build();
            chatRoomss.add(chatRoomDto);
        }


        if (chatRoomss.isEmpty()) {
            // 검색 결과가 없을 때 빈 DTO 반환
            ChatDto.chatRoomSearchListDto chatRoomDto = ChatDto.chatRoomSearchListDto.builder()
                            .roomName(null)
                            .chatMessage(null)
                            .notice("***** 채팅방 검색 결과 없음 *****")
                            .build();
            chatRoomss.add(chatRoomDto);
        }

        return new SliceImpl<>(chatRoomss);
    }

    // 채팅방 메시지로 검색하는 메소드
    public Slice<ChatDto.chatRoomSearchListDto> queryRoomsByMessage(String roomName, Long userId, Pageable pageable) {

        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;
        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;

        List<Chat.ChatMessage> chatMessageList = queryFactory.selectFrom(chatMessage)
                .leftJoin(chatMessage.chatRoom, chatRoom)
                .where(chatMessage.message.contains(roomName)
                        .and(chatRoom.user.id.eq(userId)).or(chatRoom.otherUser.id.eq(userId)))
                .orderBy(chatMessage.createdMessageAt.desc())
                .fetch();

        List<ChatDto.chatRoomSearchListDto> chatMessages = new ArrayList<>();

        for (Chat.ChatMessage chatMessageEntity : chatMessageList) {

            String roomNameDto = "";
            String profileImageUrl = "";

            if (userId.equals(chatMessageEntity.getChatRoom().getUser().getId())) {
                roomNameDto = chatMessageEntity.getChatRoom().getOtherUser().getNickname();
                profileImageUrl = chatMessageEntity.getChatRoom().getOtherUser().getProfileImage().getImgUrl();
            }

            if (userId.equals(chatMessageEntity.getChatRoom().getOtherUser().getId())){
                roomNameDto = chatMessageEntity.getUser().getNickname();
                profileImageUrl = chatMessageEntity.getUser().getProfileImage().getImgUrl();
            }

            ChatDto.chatRoomSearchListDto chatRoomDto = ChatDto.chatRoomSearchListDto.builder()
                    .roomName(roomNameDto)
                    .chatMessage(chatMessageEntity.getMessage())
                    .notice("::::: 채팅메세지 검색 결과")
                    .build();
            chatMessages.add(chatRoomDto);
        }

        if (chatMessages.isEmpty()){
            ChatDto.chatRoomSearchListDto chatRoomListDto = ChatDto.chatRoomSearchListDto.builder()
                    .roomName(null)
                    .chatMessage(null)
                    .notice("::::: 채팅메세지 검색 결과 없음 :::::")
                    .build();
            chatMessages.add(chatRoomListDto);
        }

        return new SliceImpl<>(chatMessages);
    }

    public Long getUnreadMessageCountsForUser (String userName) {
        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;
        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;

        long totalUnreadCount = 0;

        // receiver 일 때
        List<Long> receiverUnreadCounts = queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom)
                .where(chatMessage.receiver.eq(userName)
                        .and(chatMessage.isReceiverRead.eq(false)))
                .fetch();

        for (Long count : receiverUnreadCounts) {
            totalUnreadCount += count;
            log.info("receiver일때 " + totalUnreadCount);
        }

        // sender 일 때
//        List<Long> senderUnreadCounts = queryFactory
//                .select(chatMessage.count())
//                .from(chatMessage)
//                .join(chatMessage.chatRoom, chatRoom)
//                .where(chatMessage.sender.eq(userName)
//                        .and(chatMessage.isReceiverRead.eq(false)))
//                .fetch();
//
//        for (Long count : senderUnreadCounts) {
//            totalUnreadCount += count;
//            log.info("sender일때" + totalUnreadCount);
//        }

        return totalUnreadCount;

    }



}
