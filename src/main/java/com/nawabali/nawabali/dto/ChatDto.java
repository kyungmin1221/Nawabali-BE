package com.nawabali.nawabali.dto;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class ChatDto {

    @Getter
    @Setter
    @Builder
    public static class ChatMessageDto { // 채팅 메세지를 주고받기 위한 DTO
        // 메시지 타입 : 입장, 채팅
        public enum MessageType {
            ENTER, TALK
        }
        private Long id;
        private MessageType type; // 메시지 타입
        private Long roomId; // 방번호
        private Long userId;
        private String sender; // 메시지 보낸사람
        private String message; // 메시지
        private LocalDateTime createdAt;
    }

    @Getter
//    @Setter
    @Builder
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomDto {
        private Long roomId;
        private String name;
        private String roomNumber;

//        public static ChatRoom create(String name) {
//            ChatRoom chatRoom = new ChatRoom();
//            chatRoom.roomId = UUID.randomUUID().toString();
//            chatRoom.name = name;
//            return chatRoom;
//        }
    } // pub/sub 방식으로 구독자 관리 / 발송의 구현이 되므로 간소화 됐다.
}
