package com.nawabali.nawabali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.KakaoService;
import com.nawabali.nawabali.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원가입, 카카오로그인")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입에 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공")
    })
    public ResponseEntity<SignupDto.SignupResponseDto> signup(@RequestBody @Valid SignupDto.SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @GetMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse res) throws JsonProcessingException {
        kakaoService.kakaoLogin(code, res);
        return "redirect:/users/test";
    }

    @GetMapping("/test")
    public String test(){
        return "login";
    }

    @GetMapping("test1")
    public void test1(@AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println("userDetails.getUser() = " + userDetails.getUser().getEmail());
    }
}
