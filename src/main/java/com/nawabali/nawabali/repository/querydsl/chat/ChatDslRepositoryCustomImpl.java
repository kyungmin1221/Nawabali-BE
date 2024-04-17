package com.nawabali.nawabali.repository.querydsl.chat;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.domain.Chat;
import com.nawabali.nawabali.domain.QChat_ChatRoom;
import com.nawabali.nawabali.domain.QUser;
import com.nawabali.nawabali.dto.ChatDto;
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

        List <Chat.ChatRoom> chatRooms = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.user, user).fetchJoin()
                .leftJoin(chatRoom.otherUser).fetchJoin()
                .where(user.id.eq(userId).or(chatRoom.otherUser.id.eq(userId)))
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
                        if (userId.equals(newchatRoom.getUser().getId())) {
                            roomName = newchatRoom.getOtherUser().getNickname();
                        }
                        if (userId.equals(newchatRoom.getOtherUser().getId())){
                            roomName = newchatRoom.getUser().getNickname();
                        }

                        return ChatDto.ChatRoomListDto.builder()
                                .roomId(newchatRoom.getId())
                                .chatRoomEnum(newchatRoom.getChatRoomEnum())
                                .roomName(roomName)
                                .roomNumber(newchatRoom.getRoomNumber())
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


}
