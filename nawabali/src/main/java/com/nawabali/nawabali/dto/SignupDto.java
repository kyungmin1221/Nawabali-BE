package com.nawabali.nawabali.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "이메일 형식으로 입력. 조건 미충족으로 폼 제출 시 exception 발생", example = "a@a.com")
        private String email;

        @NotBlank
        @Schema(description = "이름", example = "a")
        private String username;
        @Schema(description = "닉네임, 중복불가, 특수문자 제외 3자이상 10자이하 입력. 조건 미충족으로 폼 제출 시 exception 발생", example = "안양시터줏대감")
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]{3,10}$",
                message = "특수문자 제외 3자이상 10자 이하로 입력해주세요.")
        private String nickname;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*\\W)[a-zA-Z0-9\\W]{8,15}$",
                message = "대문자, 소문자, 특수문자, 숫자 포함 8~15자리.")
        @Schema(description = "8자~15자 이내의 비밀번호. 대소문자(둘 중 하나), 숫자, 특수문자 1개씩 이상 입력. 조건 미충족으로 폼 제출 시 exception 발생.", example = "StrongP@ss123")
        @NotBlank
        private String password;
        @Schema(description = "비밀번호 확인. password 값과 일치하지 않으면 조건 미충족으로 폼 제출 시 exception 발생.")
        @NotBlank
        private String confirmPassword;
        @Schema(description = "사용자 역할.", example = "관리자라면 true")
        private boolean admin;
        @Schema(description = "이메일 인증여부. 이메일 인증 api응답에 의해 결정. false로 폼 제출 시 exception 발생.", example = "인증됐다면 true")
        private boolean certificated;

        // 주소 정보 관련
        @NotBlank
        @Schema(description = "시. Null 상태로 폼 제출시 exception 발생", example = "안양시")
        private String city;
        @NotBlank
        @Schema(description = "군, 구. Null 상태로 폼 제출시 exception 발생", example = "동안구")
        private String district;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignupResponseDto {
        @Schema(description = "유저의 pk")
        private Long id;
        @Schema(description = "회원가입 메세지", example = "회원가입이 완료되었습니다.")
        private String msg = "회원가입이 완료되었습니다";

        public SignupResponseDto(Long id) {
            this.id = id;
        }
    }
}
