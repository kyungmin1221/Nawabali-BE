package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "프로필 사진 API", description = "프로필 사진 관련 API 입니다.")
@RestController
@RequestMapping("/profileImage/users")
@RequiredArgsConstructor
public class UserImageController {

    private final ImageService imageService;

    @Operation(summary = "프로필 사진 생성", description = "userId 를 이용한 프로필 사진 생성")
    @PostMapping("/{userId}")
    public ResponseEntity<UserDto.ProfileImageDto> createProfileImage(@PathVariable Long userId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @RequestParam("file") MultipartFile file) {
        UserDto.ProfileImageDto profileImageDto = imageService.createProfileImage(userId, userDetails, file);
        return ResponseEntity.ok(profileImageDto);
    }

    @Operation(summary = "프로필 사진 수정", description = "userId 를 이용한 프로필 사진 수정")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto.ProfileImageDto> updateProfileImage(@PathVariable Long userId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @RequestParam("file") MultipartFile file) {
        UserDto.ProfileImageDto profileImageDto = imageService.updateProfileImage(userId, userDetails, file);
        return ResponseEntity.ok(profileImageDto);
    }

    @Operation(summary = "프로필 사진 삭제", description = "userId 를 이용한 프로필 사진 삭제")
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto.DeleteDto> deleteProfileImage(@PathVariable Long userId,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserDto.DeleteDto deleteDto = imageService.deleteProfileImage(userId, userDetails);
        return ResponseEntity.ok(deleteDto);
    }
}
