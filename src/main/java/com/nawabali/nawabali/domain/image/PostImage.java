package com.nawabali.nawabali.domain.image;

import com.nawabali.nawabali.domain.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class PostImage {

    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostImage(String fileName, String imgUrl, Post post) {
        this.fileName = fileName;
        this.imgUrl = imgUrl;
        this.post = post;
    }

    public void updateUrls(String fileName,String imgUrl) {
        this.fileName=fileName;
        this.imgUrl = imgUrl;

    }
}
