package com.nawabali.nawabali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nawabali.nawabali.dto.KakaoDto;
import com.nawabali.nawabali.service.KakaoService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    // 카카오 로그인 요청 처리
    @PostMapping("/kakao/login")
    public ResponseEntity<KakaoDto.signupResponseDto> kakaoLogin(@RequestParam String code,
                                               @RequestBody KakaoDto.addressRequestDto requestDto) throws JsonProcessingException {
        KakaoDto.signupResponseDto responseDto = kakaoService.kakaoLogin(code, requestDto);
        return ResponseEntity.ok(responseDto);
    }




}
