package com.nawabali.nawabali.domain;

import com.nawabali.nawabali.constant.UserRoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String password;
    private UserRoleEnum role;
    private Long kakaoId;

    @Builder
    public User(String username, String nickname, String email, String password, UserRoleEnum role) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
