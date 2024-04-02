package com.nawabali.nawabali.dto;

import lombok.*;

public class KakaoDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class signupResponseDto {
        private Long userId;
        private String username;
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class addressRequestDto {
        private String city;
        private String district;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    public static class userInfoDto{
        Long id;
        String email;
        String nickname;

        public userInfoDto(Long id, String nickname, String email) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
        }
    }
}
