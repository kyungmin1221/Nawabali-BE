package com.nawabali.nawabali.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@NoArgsConstructor
@Table (name = "likes")
@Slf4j (topic = "LikeDomain 로그")
public class Like {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean status;

    @Column
    private Long likesCount;

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn (name = "post_id")
    private Post post;

    @Builder
    public Like (boolean status, Long likesCount, User user, Post post) {
        this.status = status;
        this.likesCount = likesCount;
        this.user = user;
        this.post = post;
    }
}