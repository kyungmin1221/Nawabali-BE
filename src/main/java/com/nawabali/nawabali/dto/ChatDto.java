package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.constant.MessageType;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class ChatDto {

    @Getter
    @Setter
    @Builder
    public static class ChatMessageDto {
        private Long id;
        private MessageType type;
        private Long roomId;
        private Long userId;
        private String sender;
        private String message;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomDto {
        private Long roomId;
        private String name;
        private String roomNumber;
    } // pub/sub 방식으로 구독자 관리 / 발송의 구현이 되므로 간소화 됐다.
}
