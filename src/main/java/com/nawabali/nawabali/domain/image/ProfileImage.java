package com.nawabali.nawabali.domain.image;

import com.nawabali.nawabali.constant.DefaultProfileImage;
import com.nawabali.nawabali.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName = DefaultProfileImage.fileName;

    private String imgUrl = DefaultProfileImage.imgUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ProfileImage(User user) {
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
