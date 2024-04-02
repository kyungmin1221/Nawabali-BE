package com.nawabali.nawabali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nawabali.nawabali.dto.KakaoDto;
import com.nawabali.nawabali.security.Jwt.JwtUtil;
import com.nawabali.nawabali.service.KakaoService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;


    // 카카오 로그인 요청 처리
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code,
                                                                 HttpServletResponse response) throws JsonProcessingException {
        kakaoService.kakaoLogin(code, response);
//        return ResponseEntity.ok(responseDto);      // 사용자 수정 화면으로 넘겨줘야
        return ResponseEntity.ok().body("success");
    }

}
