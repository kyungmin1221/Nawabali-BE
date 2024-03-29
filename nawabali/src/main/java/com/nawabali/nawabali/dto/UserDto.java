package com.nawabali.nawabali.dto;

import lombok.*;

public class UserDto {
    @Getter
    @Setter
    public static class LoginRequestDto{
        String email;
        String password;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoUserInfoDto{
        Long id;
        String email;
        String nickname;

        public KakaoUserInfoDto(Long id, String nickname, String email) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
        }
    }
}
