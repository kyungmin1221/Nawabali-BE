package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.image.ProfileImage;
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProfileImageDto {
        Long id;
        String fileName;
        String imgUrl;
        User user;

        public ProfileImageDto(ProfileImage profileImage) {
            this.id = profileImage.getId();
            this.fileName = profileImage.getFileName();
            this.imgUrl = profileImage.getImgUrl();
            this.user = profileImage.getUser();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeleteDto {
        private String message;
    }
}
