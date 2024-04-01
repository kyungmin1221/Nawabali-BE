package com.nawabali.nawabali.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
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
        @Schema(description = "이메일 형식", example = "a@a.com")
        private String email;

        @Schema(description = "이름", example = "a")
        @NotBlank
        private String username;
        @Schema(description = "닉네임, 중복불가, 10자 이하", example = "안양시터줏대감")
        @NotBlank
        @Max(10)
        private String nickname;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*\\W)[a-zA-Z0-9\\W]{8,15}$",
                message = "대문자, 소문자, 특수문자, 숫자 포함 8~15자리.")
        @Schema(description = "8자~15자 이내의 비밀번호", example = "StrongP@ss123")
        @NotBlank
        private String password;
        @Schema(description = "비밀번호 확인, 폼 제출시 일치하지 않으면 exception 발생.")
        @NotBlank
        private String confirmPassword;
        @Schema(description = "사용자 역할", example = "관리자라면 true")
        @NotBlank
        private boolean admin;
        @Schema(description = "이메일 인증여부", example = "인증됐다면 true")
        @NotBlank
        private boolean certificated;

        // 주소 정보 관련
        @NotBlank
        @Schema(description = "시", example = "안양시")
        private String city;
        @NotBlank
        @Schema(description = "군, 구", example = "동안구")
        private String district;
        @NotBlank
//        @Schema(description = "", example = "")
        private String street;
        @NotBlank
//        @Schema(description = "", example = "")
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
