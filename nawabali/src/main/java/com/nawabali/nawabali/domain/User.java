package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

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
    private Long kakaoId;

    @Column(nullable = false, name = "user_rank")
    @Enumerated(EnumType.STRING)
    private UserRankEnum rank;

    @Builder
    public User(String username, String nickname, String email, String password, UserRoleEnum role, Address address, UserRankEnum rank) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.rank = rank;
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

    public void update(UserDto.UserInfoRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.address = new Address(requestDto.getCity(), requestDto.getDistrict());
        this.password = requestDto.getPassword();
    }
}
