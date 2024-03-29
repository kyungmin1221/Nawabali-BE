package com.nawabali.nawabali.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@Table (name = "comment")
@Slf4j(topic = "CommentDomain 로그")
public class Comment {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(length = 300)
    private String contents;

    @Column (updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne (cascade = CascadeType.REMOVE)
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne (cascade = CascadeType.REMOVE)
    @JoinColumn (name = "post_id")
    private Post post;

    @Builder
    public Comment (String contents, User user, Post post){
        this.contents = contents;
        this.user = user;
        this.post = post;
        this.createdAt = LocalDateTime.now();
    }

}