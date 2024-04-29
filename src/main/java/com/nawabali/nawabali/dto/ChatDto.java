package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.constant.ChatRoomEnum;
//import com.nawabali.nawabali.constant.MessageType;
import com.nawabali.nawabali.domain.Chat;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class ChatDto {

    @Getter
    @Setter
    @Builder
    public static class ChatMessageDto {
        private Long id;
//        private Long roomId;
//        private Long userId;
//        private String sender;
        private Chat.ChatMessage.MessageType type;
        private String message;
        private LocalDateTime createdMessageAt;
    }

    @Getter
    @Setter
    @Builder
    public static class ChatMessageResponseDto {
        private Long id; // message id
        private Long roomId;
        private Long userId;
        private String sender;
        private String message;
        private String receiver;
        private boolean isReceiverRead;
        private boolean isRead;
        private LocalDateTime createdMessageAt;
    }

    @Getter
    @Builder
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomDto {
        private Long roomId;
        private String roomName;
        private String roomNumber;
        private ChatRoomEnum chatRoomEnum;
        private Long userId;
        private Long otherUserId;
        private String profileImageUrl;
    } // pub/sub 방식으로 구독자 관리 / 발송의 구현이 되므로 간소화 됐다.

    @Getter
    @Builder
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomListDto {
        private Long roomId;
        private String roomName;
        private String roomNumber;
        private ChatRoomEnum chatRoomEnum;
        private String chatMessage;
        private Long messageId;
        private String profileImageUrl;
        private Long unreadCount;
    }
}
