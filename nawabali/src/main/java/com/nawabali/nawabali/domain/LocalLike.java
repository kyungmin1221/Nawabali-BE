package com.nawabali.nawabali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
@Getter
@NoArgsConstructor
@Table (name = "localLikes")
@Slf4j (topic = "LocalLikeEntity 로그")
public class LocalLike {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private boolean status;

    @Column
    private Long localLikesCount;

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn (name = "post_id")
    private Post post;

    @Builder
    public LocalLike (boolean status, long localLikesCount, User user, Post post) {
        this.status = status;
        this.localLikesCount = localLikesCount;
        this.user = user;
        this.post = post;
    }
}
