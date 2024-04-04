package com.nawabali.nawabali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nawabali.nawabali.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    // 카카오 로그인 요청 처리
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code,
                                        HttpServletResponse response)
            throws JsonProcessingException {

        kakaoService.kakaoLogin(code , response);
        return ResponseEntity.ok().body("success");

    }
}
