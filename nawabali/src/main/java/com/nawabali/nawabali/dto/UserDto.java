package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.image.ProfileImage;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @Builder
    @AllArgsConstructor
    public static class UserInfoResponseDto{
        Long id;
        String email;
        String nickname;
        String city;
        String district;
        UserRankEnum rank;
        Long localCount;
        Long likesCount;


        public UserInfoResponseDto(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.city = user.getAddress().getCity();
            this.district = user.getAddress().getDistrict();
            this.rank = user.getRank();

        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserInfoRequestDto {
        @NotBlank
        String nickname;
        @NotBlank
        String city;
        @NotBlank
        String district;
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*\\W)[a-zA-Z0-9\\W]{8,15}$",
                message = "대문자, 소문자, 특수문자, 숫자 포함 8~15자리.")
        String password;
    }

    @Getter
    @Setter
    public static class deleteResponseDto {
        final String message = "회원탈퇴가 완료되었습니다.";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProfileImageDto {
        Long id;
        String fileName;
        String imgUrl;
        Long userId;

        public ProfileImageDto(ProfileImage profileImage) {
            this.id = profileImage.getId();
            this.fileName = profileImage.getFileName();
            this.imgUrl = profileImage.getImgUrl();
            this.userId = profileImage.getUser().getId();
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
