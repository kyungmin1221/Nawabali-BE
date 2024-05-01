package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.QChat_ChatMessage;
import com.nawabali.nawabali.domain.QChat_ChatRoom;
import com.nawabali.nawabali.domain.QUser;
import com.nawabali.nawabali.domain.image.QProfileImage;
import com.nawabali.nawabali.dto.ChatDto;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.nawabali.nawabali.constant.ChatRoomEnum.GROUP;

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
                        Long profileImage = null;
                        if (userId.equals(newchatRoom.getUser().getId())) {
                            roomName = newchatRoom.getOtherUser().getNickname();
                            profileImage = newchatRoom.getOtherUser().getProfileImage().getId();
                        }
                        if (userId.equals(newchatRoom.getOtherUser().getId())){
                            roomName = newchatRoom.getUser().getNickname();
                            profileImage = newchatRoom.getUser().getProfileImage().getId();
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
                                .chatRoomEnum(newchatRoom.getChatRoomEnum())
                                .roomName(roomName)
                                .roomNumber(newchatRoom.getRoomNumber())
                                .chatMessage(latestMessageContent)
                                .profileImageId(profileImage)
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
                        .profileImageId(newchatroom.getUser().getProfileImage().getId())
                        .build())
                .collect(Collectors.toList());


        return new SliceImpl<>(chatRoomListDtos, pageable, hasNext);
    }


}
