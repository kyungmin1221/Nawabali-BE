package com.nawabali.nawabali.domain.image;

import com.nawabali.nawabali.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String imgUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public ProfileImage(Long id, String fileName, String imgUrl, User user) {
        this.id = id;
        this.fileName = fileName;
        this.imgUrl = imgUrl;
        this.user = user;
    }

    public void updateFileName(String fileName) {
        this.fileName = fileName;
    }

    public void updateImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
