package com.nawabali.nawabali.controller;


import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.security.Jwt.JwtUtil;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.KakaoService;
import com.nawabali.nawabali.service.UserService;
import io.jsonwebtoken.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원가입, 로그인 관련 API 입니다.")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입에 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공")
    })
    public ResponseEntity<SignupDto.SignupResponseDto> signup(@RequestBody @Valid SignupDto.SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @Operation(summary = "회원 정보 조회", description = "회원 정보 조회에 사용하는 API, 조회하려는 유저의 PK값 필요.")
    @GetMapping("/{userId}")
    public UserDto.UserInfoResponseDto getUserInfo(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getUserInfo(userId, userDetails.getUser());
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정에 사용하는 API, 수정하려는 유저의 PK값 필요.")
    @PatchMapping("/{userId}")
    public UserDto.UserInfoResponseDto updateUserInfo(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserDto.UserInfoRequestDto userInfoRequestDto){
        return userService.updateUserInfo(userId, userDetails.getUser(), userInfoRequestDto);
    }


    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴에 사용하는 API, 탈퇴하려는 유저의 PK값 필요.")
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto.deleteResponseDto> deleteUserInfo(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUserInfo(userId, userDetails.getUser());
    }

    @Operation(summary = "닉네임 중복검사", description = "닉네임 중복여부를 boolean 값으로 반환.")
    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestBody String nickname){
        return userService.checkNickname(nickname);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
//        userService.logout(request, response);
        return "success";
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
