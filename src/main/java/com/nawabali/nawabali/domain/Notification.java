package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.LikeCategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Table (name = "notification")
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private LocalDateTime createdAt;

    @Column (nullable = false)
    private String sender;

    @Column (nullable = false)
    private String contents;

//    @ManyToOne (fetch = FetchType.LAZY)
//    @JoinColumn (name = "post_id")
//    private Post post;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "user_Id")
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "like_id")
    private Like like;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "chatRoom_Id")
    private Chat.ChatRoom chatRoom;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "comment_Id")
    private Comment comment;

}
