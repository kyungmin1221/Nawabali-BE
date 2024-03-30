package com.nawabali.nawabali.domain.image;

import com.nawabali.nawabali.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@NoArgsConstructor
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostImage(String fileName, String imgUrl, Post post) {
        this.fileName = fileName;
        this.imgUrl = imgUrl;
        this.post = post;
    }
}
