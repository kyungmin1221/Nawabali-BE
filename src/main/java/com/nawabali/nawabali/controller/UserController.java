package com.nawabali.nawabali.controller;


import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.domain.elasticsearch.UserSearch;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원가입, 로그인 관련 API 입니다.")
public class UserController {
    private final UserService userService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam(name = "accessToken", required = false) String accessToken){
        return userService.logout(accessToken);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입에 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공")
    })
    public ResponseEntity<SignupDto.SignupResponseDto> signup(@RequestBody @Valid SignupDto.SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @Operation(summary = "내 정보 조회", description = "내 정보 조회에 사용하는 API")
    @GetMapping("/my-info")
    public UserDto.UserInfoResponseDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getUserInfo(userDetails.getUser());
    }

    @Operation(summary = "유저 정보 조회", description = "회원 정보 조회에 사용하는 API")
    @GetMapping("/info")
    public UserDto.UserInfoResponseDto getUserInfo(@RequestParam(name = "userId") Long userId) {
        return userService.getUserInfo(userId);
    }

    @Operation(summary = "회원 정보 수정", description = "내 정보 수정에 사용하는 API")
    @PatchMapping("/my-info")
    public UserDto.UserInfoResponseDto updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserDto.UserInfoRequestDto userInfoRequestDto){
        return userService.updateUserInfo(userDetails.getUser(), userInfoRequestDto);
    }


    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴에 사용하는 API")
    @DeleteMapping("/my-info")
    public ResponseEntity<UserDto.deleteResponseDto> deleteUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUserInfo(userDetails.getUser());
    }

    @Operation(summary = "닉네임 중복검사", description = "닉네임 중복여부를 boolean 값으로 반환.")
    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam("nickname") String nickname){
        return userService.checkNickname(nickname);
    }

    @Operation(summary = "정보 수정을 위한 비밀 번호 확인", description = "입력한 비밀번호와 현재 비밀번호를 비교하여 boolean 값으로 반환")
    @GetMapping("/check-myPassword")
    public boolean checkMyPassword(@RequestParam("inputPassword") String inputPassword, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.checkMyPassword(inputPassword, userDetails.getUser());
    }
    @Operation(summary = "내가 쓴 게시글 조회", description = "10장씩 조회, 작성일 순으로 정렬, 카테고리로 필터링. 선택 안할시 전체 조회.")
    @GetMapping("/my-posts")
    public Slice<PostDto.ResponseDto> getMyPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name ="category",required = false) Category category)
    {
        return userService.getMyPosts(userDetails.getUser(), pageable, category);
    }
    @Operation(summary = "채팅 할 회원의 닉네임 검색", description = "회원의 닉네임을 검색하면 PK, 프로필, 지역, 등급 반환")
    @GetMapping("/search")
    public List<UserSearch> searchNickname(@RequestParam(name = "nickname", required = false) String nickname){
        return userService.searchNickname(nickname);
    }


}
