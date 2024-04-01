package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.image.ProfileImage;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    @Embedded
    private Address address;

    private Long kakaoId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfileImage profileImage;

    @Builder
    public User(String username, String nickname, String email, String password, UserRoleEnum role, Address address, ProfileImage profileImage) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.profileImage = profileImage;

        if (profileImage != null) {
            profileImage.setUser(this);
        }
    }

    @Builder
    public User(String nickname, String email, Long kakaoId){
        this.nickname = nickname;
        this.email = email;
        this.kakaoId = kakaoId;
    }

    public void updateKakaoId(Long id) {
        this.kakaoId = id;
    }

}
