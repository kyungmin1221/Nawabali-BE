package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.UserDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private Long kakaoId;

    @Column(nullable = false, name = "user_rank")
    @Enumerated(EnumType.STRING)
    private UserRankEnum rank;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfileImage profileImage;

    @OneToMany(mappedBy = "user" ,  fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<BookMark> bookMarks = new ArrayList<>();

    @Builder
    public User(Long kakaoId, String nickname, String email, String password,
                UserRoleEnum role, Address address, UserRankEnum rank, ProfileImage profileImage) {
        this.kakaoId = kakaoId;
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

    public void updateKakaoId(Long kakoId) {
        this.kakaoId = id;
    }

    public void update(UserDto.UserInfoRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.address = new Address(requestDto.getCity(), requestDto.getDistrict());
        this.password = requestDto.getPassword();
    }
}
