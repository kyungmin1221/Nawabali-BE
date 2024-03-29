package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.constant.Town;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Category category;

    @Embedded
    @Column(nullable = false)
    private Town town;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder(toBuilder = true)
    public Post(String title, String contents, LocalDateTime createdAt, LocalDateTime modifiedAt,
                Category category, Town town, User user) {

        this.title = title;
        this.contents = contents;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.category = category;
        this.town = town;
        this.user = user;
    }

    public void update(String title, String contents, Category category, Town town) {
        this.title = title;
        this.contents = contents;
        this.category = category;
        this.town = town;
    }

}
