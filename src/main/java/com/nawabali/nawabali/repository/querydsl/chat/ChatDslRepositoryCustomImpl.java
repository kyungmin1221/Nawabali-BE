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

import java.util.ArrayList;
import java.util.List;
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

    public Slice <ChatDto.ChatRoomListDto> findChatRoomByRoomName (String roomName, Pageable pageable) {

        QChat_ChatRoom chatRoom = QChat_ChatRoom.chatRoom;
        QUser user = QUser.user;
        QChat_ChatMessage chatMessage = QChat_ChatMessage.chatMessage;
        QProfileImage profileImage = QProfileImage.profileImage;

        // 서브쿼리로 각 채팅방의 최신 메시지를 가져옵니다.
//        var latestMessage = JPAExpressions
//                .selectFrom(chatMessage)
//                .where(chatMessage.chatRoom.eq(chatRoom))
//                .orderBy(chatMessage.createdMessageAt.desc())
//                .limit(1);


        List <Chat.ChatRoom> chatRooms = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.user, user).fetchJoin()
                .leftJoin(chatRoom.chatMessageList, chatMessage)
                .where(chatRoom.roomName.contains(roomName)
                        .or(chatRoom.otherUser.nickname.contains(roomName))
                        .or(chatRoom.otherUser.isNotNull().and(chatRoom.otherUser.nickname.contains(roomName))))
//                        .and(chatMessage.in(latestMessage)))
                .orderBy(chatMessage.createdMessageAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = chatRooms.size() > pageable.getPageSize();

        if (hasNext) {
            chatRooms.remove(chatRooms.size() -1);
        }

        List <ChatDto.ChatRoomListDto> chatRoomListDtos = chatRooms.stream()
                .map(newchatroom -> ChatDto.ChatRoomListDto.builder()
                        .roomId(newchatroom.getId())
//                        .chatMessage(newchatroom.getChatMessageList().get(0).toString())
                        .roomName(newchatroom.getOtherUser().getNickname())
                        .profileImageUrl(newchatroom.getUser().getProfileImage().getImgUrl())
                        .build())
                .collect(Collectors.toList());


        return new SliceImpl<>(chatRoomListDtos, pageable, hasNext);
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
