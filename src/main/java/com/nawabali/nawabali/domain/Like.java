package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.LikeCategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder (toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "likes")
public class Like {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private boolean status;

    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private LikeCategoryEnum likeCategoryEnum;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "post_id")
    private Post post;

}