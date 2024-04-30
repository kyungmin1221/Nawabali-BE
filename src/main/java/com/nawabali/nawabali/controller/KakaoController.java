package com.nawabali.nawabali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nawabali.nawabali.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    // 카카오 로그인 요청 처리
    @GetMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam String code,
                                                                    HttpServletResponse response)
            throws JsonProcessingException, IOException {

       String accessToken = kakaoService.kakaoLogin(code, response);

//       return "redirect:https://www.dongnaebangnae.com/?accessToken=" + accessToken;
        return "redirect: /";
//       return "redirect:https://www.dongnaebangnae.com";

    }
}
