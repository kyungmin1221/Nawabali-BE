package com.nawabali.nawabali.controller;


import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.KakaoService;
import com.nawabali.nawabali.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @GetMapping("/{userId}")
    public UserDto.UserInfoResponseDto getUserInfo(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getUserInfo(userId, userDetails.getUser());
    }

    @PatchMapping("/{userId}")
    public UserDto.UserInfoResponseDto updateUserInfo(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserDto.UserInfoRequestDto userInfoRequestDto){
        return userService.updateUserInfo(userId, userDetails.getUser(), userInfoRequestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto.deleteResponseDto> deleteUserInfo(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUserInfo(userId, userDetails.getUser());
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
                                                                      @RequestParam("file") MultipartFile file) {
        UserDto.ProfileImageDto profileImageDto = userService.createProfileImage(userId, userDetails, file);
        return ResponseEntity.ok(profileImageDto);
    }

    @PatchMapping("/{userId}/profileImage")
    public ResponseEntity<UserDto.ProfileImageDto> updateProfileImage(@PathVariable Long userId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @RequestParam("file") MultipartFile file) {
        UserDto.ProfileImageDto profileImageDto = userService.updateProfileImage(userId, userDetails, file);
        return ResponseEntity.ok(profileImageDto);
    }

    @DeleteMapping("/{userId}/profileImage")
    public ResponseEntity<UserDto.DeleteDto> deleteProfileImage(@PathVariable Long userId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserDto.DeleteDto deleteDto = userService.deleteProfileImage(userId, userDetails);
        return ResponseEntity.ok(deleteDto);
    }
}
