package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.dto.UserDto;
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

    @Column
    private String kakaoId;

    @Column(nullable = false, name = "user_rank")
    @Enumerated(EnumType.STRING)
    private UserRankEnum rank;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfileImage profileImage;

    @Builder
    public User(String username, String nickname, String email, String password, UserRoleEnum role, Address address, UserRankEnum rank, ProfileImage profileImage) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.rank = rank;
        this.profileImage = profileImage;

        if (profileImage != null) {
            profileImage.setUser(this);
        }
    }

    @Builder
    public User(String nickname, String email, String kakaoId){
        this.nickname = nickname;
        this.email = email;
        this.kakaoId = kakaoId;
    }

    public void updateKakaoId(String id) {
        this.kakaoId = id;
    }

    public void update(UserDto.UserInfoRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.address = new Address(requestDto.getCity(), requestDto.getDistrict());
        this.password = requestDto.getPassword();
    }
}
