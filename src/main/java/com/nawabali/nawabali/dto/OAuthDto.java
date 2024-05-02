package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class OAuthDto {

    @Getter
    @AllArgsConstructor
    public static class oAuthResponseDto{
        private boolean authenticated;
        private Long id;
        private String nickname;
        private String imgUrl;
        private String district;

        public oAuthResponseDto(User existUser) {
            this.authenticated = true;
            this.id = existUser.getId();
            this.nickname= existUser.getNickname();
            this.imgUrl = existUser.getProfileImage().getImgUrl();
            this.district = existUser.getAddress().getDistrict();
        }
    }
}
