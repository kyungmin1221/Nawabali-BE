package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
//    private final KakaoService kakaoService;

    @PostMapping("/signup")
    public SignupDto.SignupResponseDto signup(@RequestBody SignupDto.SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @GetMapping("/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse res) {
//        kakaoService.login(code, res);}
    }
}
