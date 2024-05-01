package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Chat {

    @Entity
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "chatMessage")
    public static class ChatMessage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Id;

        public enum MessageType { ENTER, TALK }

        @Column (nullable = false)
        private String sender;

        @Column (nullable = false)
        private boolean isRead;

        @Column (nullable = false)
        private String message;

        @Column (nullable = false)
        private LocalDateTime createdMessageAt;

        @Column (nullable = false)
        private String receiver;

        @Column (nullable = false)
        private boolean isReceiverRead;

        @ManyToOne (fetch = FetchType.LAZY)
        @JoinColumn (name = "user_id")
        private User user;

        @ManyToOne (fetch = FetchType.LAZY)
        @JoinColumn (name = "room_id")
        private ChatRoom chatRoom;

        public void receiverRead (boolean isReceiverRead) {
            this.isReceiverRead = isReceiverRead;
        }
    }

    @Entity
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "chatRoom")
    public static class ChatRoom {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Id;

        @Column (nullable = false)
        @Enumerated (EnumType.STRING)
        private ChatRoomEnum chatRoomEnum;

        @Column (nullable = false)
        private String roomName;

        @ManyToOne (fetch = FetchType.LAZY)
        @JoinColumn (name = "user_id")
        private User user;

        @ManyToOne (fetch = FetchType.LAZY)
        @JoinColumn (name = "other_user_id")
        private User otherUser;

        @OneToMany (mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ChatMessage> chatMessageList = new ArrayList<>();

        public Optional<ChatMessage> getLatestMessage() {
            if (chatMessageList.isEmpty()) { return Optional.empty();
            } else { return Optional.of(chatMessageList.get(chatMessageList.size() - 1));}
        }
    }
}
