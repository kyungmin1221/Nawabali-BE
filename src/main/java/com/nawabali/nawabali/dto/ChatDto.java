package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.domain.Chat.ChatMessage;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class ChatDto {

    @Getter
    @Builder
    public static class ChatMessageDto {
        private Long id;
        private ChatMessage.MessageType type;
        private String message;
        private LocalDateTime createdMessageAt;
    }

    @Getter
    @Builder
    public static class ChatMessageResponseDto {
        private Long id;
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
        private ChatRoomEnum chatRoomEnum;
        private Long userId;
        private String roomName;
        private String profileImageUrl;
        private Long otherUserId;
    }

    @Getter
    @Builder
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomListResponseDto {
        private Long roomId;
        private String roomName;
        private String profileImageUrl;
        private String chatMessage;
        private Long unreadCount;
    }

    @Getter
    @Builder
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomSearchListDto {
        private String roomName;
        private String chatMessage;
        private String notice;
    }

    @Getter
    @Builder
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageInfo {
        private String roomName;
        private String profileImageUrl;
        private Long unreadCount;
    }
}
