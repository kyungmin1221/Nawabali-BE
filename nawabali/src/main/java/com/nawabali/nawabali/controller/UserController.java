package com.nawabali.nawabali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.KakaoService;
import com.nawabali.nawabali.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @PostMapping("/signup")
    public SignupDto.SignupResponseDto signup(@RequestBody SignupDto.SignupRequestDto requestDto) {
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

    @PostMapping("/{userId}/profileImage")
    public ResponseEntity<UserDto.ProfileImageDto> createProfileImage(@PathVariable Long userId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      MultipartFile multipartFile) {
        UserDto.ProfileImageDto profileImageDto = userService.createProfileImage(userId, userDetails, multipartFile);
        return ResponseEntity.ok(profileImageDto);
    }

    @PatchMapping("/{userId}/profileImage")
    public ResponseEntity<UserDto.ProfileImageDto> updateProfileImage(@PathVariable Long userId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      MultipartFile multipartFile) {
        UserDto.ProfileImageDto profileImageDto = userService.updateProfileImage(userId, userDetails, multipartFile);
        return ResponseEntity.ok(profileImageDto);
    }

    @DeleteMapping("/{userId}/profileImage")
    public ResponseEntity<UserDto.DeleteDto> deleteProfileImage(@PathVariable Long userId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserDto.DeleteDto deleteDto = userService.deleteProfileImage(userId, userDetails);
        return ResponseEntity.ok(deleteDto);
    }
}
