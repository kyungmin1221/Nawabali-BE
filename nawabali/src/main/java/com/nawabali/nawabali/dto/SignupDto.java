package com.nawabali.nawabali.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

public class SignupDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignupRequestDto{

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String username;

        @NotBlank
        private String nickname;

//        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*\\W)[a-zA-Z0-9\\W]{8,15}$",
//                message = "대문자, 소문자, 특수문자, 숫자 포함 8~15자리.")
        private String password;

        private String confirmPassword;

        private boolean admin;

        private boolean certificated;

        // 주소 정보 관련
        private String city;

        private String district;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignupResponseDto {
        private Long id;
        private String msg = "회원가입이 완료되었습니다";

        public SignupResponseDto(Long id) {
            this.id = id;
        }
    }
}
