package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.dto.ChatDto;
//import com.nawabali.nawabali.constant.MessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Chat {

    @Entity
    @Getter
    @Setter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "chatMessage")
    @Slf4j(topic = "chat 로그")
    public static class ChatMessage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Id;

        @Column (nullable = false)
        private String sender; // 메시지 보낸사람

        @Column (nullable = false)
        private String message; // 메시지

        @Column (nullable = false)
        private LocalDateTime createdMessageAt;

        @Column (nullable = false)
        private String receiver;

        @Column (nullable = false)
        private boolean isRead;

        @Column (nullable = false)
        private boolean isReceiverRead;

        @ManyToOne (fetch = FetchType.LAZY)
        @JoinColumn (name = "user_id")
        private User user;

        @ManyToOne (fetch = FetchType.LAZY)
        @JoinColumn (name = "room_id")
        private ChatRoom chatRoom;

        private ChatMessage (User user) {
            this.sender = user.getNickname();
        }

        public void update(boolean read) {
            this.isRead = read;
        }

    }

    @Entity
    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "chatRoom")
    @Slf4j(topic = "chat 로그")
    public static class ChatRoom {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Id;

        @Column(nullable = false)
        private String roomNumber;

        @Column (nullable = false)
        private String roomName;

        @Column (nullable = false)
        @Enumerated (EnumType.STRING)
        private ChatRoomEnum chatRoomEnum;

        @ManyToOne (fetch = FetchType.LAZY)
        @JoinColumn (name = "user_id")
        private User user;

        @ManyToOne (fetch = FetchType.LAZY)
        @JoinColumn (name = "other_user_id")
        private User otherUser;

        @OneToMany (mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ChatMessage> chatMessageList = new ArrayList<>();

        public Optional<ChatMessage> getLatestMessage() {
            // chatMessageList가 비어있는지 확인
            if (chatMessageList.isEmpty()) {
                return Optional.empty(); // chatMessageList가 비어있으면 빈 Optional 반환
            } else {
                // chatMessageList가 비어있지 않으면 가장 최근의 메시지를 반환
                return Optional.of(chatMessageList.get(chatMessageList.size() - 1));
            }
        }
    }
}
