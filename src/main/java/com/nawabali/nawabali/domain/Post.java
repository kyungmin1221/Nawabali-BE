package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.constant.Town;
import com.nawabali.nawabali.domain.image.PostImage;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String contents;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Category category;

    @Embedded
    @Valid
    private Town town;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();


    @Builder
    public Post(String contents, LocalDateTime createdAt, LocalDateTime modifiedAt,
                Category category, Town town, User user) {

        this.contents = contents;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.category = category;
        this.town = town;
        this.user = user;
    }

    public void update(String contents) {
        this.contents = contents;
    }


}
