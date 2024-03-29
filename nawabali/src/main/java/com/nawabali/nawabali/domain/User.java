package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRoleEnum;
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

    private Long kakaoId;

    @Builder
    public User(String username, String nickname, String email, String password, UserRoleEnum role, Address address) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
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
