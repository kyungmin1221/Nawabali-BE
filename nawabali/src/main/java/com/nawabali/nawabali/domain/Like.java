package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.LikeCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "likes")
@Slf4j (topic = "LikeDomain 로그")
public class Like {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private LikeCategory likeCategory;

    @Column (nullable = false)
    private boolean status;

    @Column (nullable = false)
    private Long likesCount;

    @ManyToOne (fetch = FetchType.LAZY) // 성능향상에 좋다.
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "post_id")
    private Post post;

}