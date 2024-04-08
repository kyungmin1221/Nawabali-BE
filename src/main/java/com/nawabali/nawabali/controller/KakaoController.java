package com.nawabali.nawabali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    // 카카오 로그인 요청 처리
    @GetMapping("/kakao/callback")
    public ResponseEntity<UserDto.kakaoLoginResponseDto> kakaoLogin(@RequestParam String code,
                                                                    HttpServletResponse response)
            throws JsonProcessingException, IOException {

        return kakaoService.kakaoLogin(code, response);

    }
}
