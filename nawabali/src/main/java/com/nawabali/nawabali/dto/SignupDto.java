package com.nawabali.nawabali.dto;

import jakarta.validation.constraints.Email;
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
        private String email;

//        @Schema(description = "이름", example = "steeve")
        private String username;

        private String nickname;

//        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*\\W)[a-zA-Z0-9\\W]{8,15}$",
//                message = "대문자, 소문자, 특수문자, 숫자 포함 8~15자리.")
//        @Schema(description = "8자~15자 이내의 비밀번호", example = "StrongP@ss123")
        private String password;
        private String confirmPassword;
//        @Schema(description = "사용자 역할", example = "ADMIN")
        private boolean admin;

        private boolean certificated;

        // 주소 정보 관련
        private String city;

        private String district;

        private String street;

        private String zipcode;
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
